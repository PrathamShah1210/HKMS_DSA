package com.hkms.service;

import com.hkms.core.avl.AvlTree;
import com.hkms.core.heap.MaxHeap;
import com.hkms.core.nary.NAryNode;
import com.hkms.core.nary.NAryTree;
import com.hkms.core.nary.NodeType;
import com.hkms.core.trie.Trie;
import com.hkms.model.dto.CategoryDTO;
import com.hkms.model.dto.PaperDTO;
import com.hkms.model.entity.CategoryEntity;
import com.hkms.model.entity.PaperEntity;
import com.hkms.repository.CategoryRepository;
import com.hkms.repository.PaperRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Coordinates and synchronizes the durable SQLite persistence layer
 * with the four in-memory custom data structures:
 * 1. N-Ary Tree (Hierarchical Categories & Paper References)
 * 2. AVL Tree (Self-balancing index of papers by ID)
 * 3. Prefix Trie (Fast title, keyword, and author autocomplete)
 * 4. Max Heap (Priority-based paper ranking)
 */
@Service
public class DsaSyncManager {

    private static final Logger log = LoggerFactory.getLogger(DsaSyncManager.class);

    private final CategoryRepository categoryRepository;
    private final PaperRepository paperRepository;

    // Pure custom DSA instances
    private final NAryTree hierarchyTree;
    private final AvlTree<Long, PaperDTO> paperIndex;
    private final Trie searchTrie;
    private final MaxHeap<PaperDTO> priorityHeap;

    public DsaSyncManager(CategoryRepository categoryRepository, PaperRepository paperRepository) {
        this.categoryRepository = categoryRepository;
        this.paperRepository = paperRepository;

        this.hierarchyTree = new NAryTree();
        this.paperIndex = new AvlTree<>();
        this.searchTrie = new Trie();
        this.priorityHeap = new MaxHeap<>();
    }

    public NAryTree getHierarchyTree() {
        return hierarchyTree;
    }

    public AvlTree<Long, PaperDTO> getPaperIndex() {
        return paperIndex;
    }

    public Trie getSearchTrie() {
        return searchTrie;
    }

    public MaxHeap<PaperDTO> getPriorityHeap() {
        return priorityHeap;
    }

    /**
     * Cold-start initialization: reads all categories and papers from SQLite
     * and constructs all four in-memory data structures from scratch.
     */
    @EventListener(ApplicationReadyEvent.class)
    public synchronized void rebuildAllFromDatabase() {
        log.info("Starting in-memory DSA reconstruction from SQLite database...");

        // 1. Clear in-memory structures
        hierarchyTree.clear();
        paperIndex.clear();
        searchTrie.clear();
        priorityHeap.clear();

        // 2. Build N-Ary Tree hierarchy from categories
        List<CategoryEntity> allCategories = categoryRepository.findAll();
        if (!allCategories.isEmpty()) {
            buildNAryTreeFromCategories(allCategories);
        } else {
            // Seed a default root node
            NAryNode defaultRoot = new NAryNode("root", "Computer Science Knowledge Base", NodeType.ROOT);
            hierarchyTree.insert(null, defaultRoot);
        }

        // 3. Load all papers from SQLite and synchronize into all 4 data structures
        List<PaperEntity> allPapers = paperRepository.findAll();
        Map<String, String> categoryNames = new HashMap<>();
        for (CategoryEntity c : allCategories) {
            categoryNames.put(c.getId(), c.getName());
        }

        for (PaperEntity p : allPapers) {
            PaperDTO dto = toDTO(p, categoryNames.get(p.getCategoryId()));

            // A. Index in AVL Tree
            paperIndex.insert(dto.getId(), dto);

            // B. Attach reference leaf in N-Ary Tree
            NAryNode paperNode = new NAryNode("paper-" + dto.getId(), dto.getTitle(), NodeType.PAPER, dto.getId());
            if (hierarchyTree.findById(dto.getCategoryId()) != null) {
                hierarchyTree.insert(dto.getCategoryId(), paperNode);
            }

            // C. Index in Trie
            indexPaperInTrie(dto);

            // D. Push into Max Heap
            priorityHeap.insert(dto.getId(), dto.getPriorityScore(), dto);
        }

        log.info("In-memory DSA rebuild completed successfully: " +
                "N-Ary Nodes: {}, AVL Size: {}, Trie Words: {}, Heap Items: {}",
                hierarchyTree.size(), paperIndex.size(), searchTrie.getWordCount(), priorityHeap.size());
    }

    private void buildNAryTreeFromCategories(List<CategoryEntity> categories) {
        // Find roots (categories where parentId is null or equals 'root')
        CategoryEntity rootCat = categories.stream()
                .filter(c -> c.getParentId() == null || c.getId().equalsIgnoreCase("root"))
                .findFirst()
                .orElse(null);

        if (rootCat != null) {
            hierarchyTree.insert(null, new NAryNode(rootCat.getId(), rootCat.getName(), rootCat.getType()));
        } else {
            NAryNode systemRoot = new NAryNode("root", "Computer Science Knowledge Base", NodeType.ROOT);
            hierarchyTree.insert(null, systemRoot);
        }

        // Insert remaining categories level by level or with retries until all resolved
        Set<String> inserted = new HashSet<>();
        inserted.add(hierarchyTree.getRoot().getId());

        boolean progress = true;
        while (progress) {
            progress = false;
            for (CategoryEntity cat : categories) {
                if (!inserted.contains(cat.getId())) {
                    String parentId = cat.getParentId();
                    if (parentId == null) {
                        parentId = hierarchyTree.getRoot().getId();
                    }
                    if (inserted.contains(parentId)) {
                        NAryNode node = new NAryNode(cat.getId(), cat.getName(), cat.getType());
                        hierarchyTree.insert(parentId, node);
                        inserted.add(cat.getId());
                        progress = true;
                    }
                }
            }
        }
    }

    /**
     * Synchronizes a newly added research paper across all data structures.
     */
    public synchronized void syncAddPaper(PaperDTO dto) {
        // 1. AVL Tree insertion
        paperIndex.insert(dto.getId(), dto);

        // 2. N-Ary Tree leaf node insertion
        NAryNode paperNode = new NAryNode("paper-" + dto.getId(), dto.getTitle(), NodeType.PAPER, dto.getId());
        if (hierarchyTree.findById(dto.getCategoryId()) != null) {
            hierarchyTree.insert(dto.getCategoryId(), paperNode);
        }

        // 3. Trie indexing
        indexPaperInTrie(dto);

        // 4. Max Heap insertion
        priorityHeap.insert(dto.getId(), dto.getPriorityScore(), dto);
    }

    /**
     * Synchronizes an updated paper across data structures.
     */
    public synchronized void syncUpdatePaper(PaperDTO oldDto, PaperDTO newDto) {
        // 1. AVL Tree update
        paperIndex.insert(newDto.getId(), newDto);

        // 2. N-Ary Tree move if category changed
        String nodeRefId = "paper-" + newDto.getId();
        if (!oldDto.getCategoryId().equals(newDto.getCategoryId())) {
            hierarchyTree.delete(nodeRefId);
            NAryNode updatedNode = new NAryNode(nodeRefId, newDto.getTitle(), NodeType.PAPER, newDto.getId());
            if (hierarchyTree.findById(newDto.getCategoryId()) != null) {
                hierarchyTree.insert(newDto.getCategoryId(), updatedNode);
            }
        } else {
            NAryNode node = hierarchyTree.findById(nodeRefId);
            if (node != null) {
                node.setName(newDto.getTitle());
            }
        }

        // 3. Trie index update: delete old words, insert new words
        unindexPaperFromTrie(oldDto);
        indexPaperInTrie(newDto);

        // 4. Max Heap priority update
        priorityHeap.updatePriority(newDto.getId(), newDto.getPriorityScore());
        // Also update stored payload
        for (var item : priorityHeap.getElements()) {
            if (item.getId().equals(newDto.getId())) {
                item.setPayload(newDto);
                break;
            }
        }
    }

    /**
     * Synchronizes paper deletion across data structures.
     */
    public synchronized void syncDeletePaper(Long paperId, String categoryId, String title, String keywords) {
        // 1. AVL Tree delete
        paperIndex.delete(paperId);

        // 2. N-Ary Tree delete
        hierarchyTree.delete("paper-" + paperId);

        // 3. Trie un-indexing
        PaperDTO temp = new PaperDTO();
        temp.setId(paperId);
        temp.setTitle(title);
        temp.setKeywords(keywords);
        unindexPaperFromTrie(temp);

        // 4. Max Heap delete
        priorityHeap.delete(paperId);
    }

    /**
     * Synchronizes priority update in Max Heap.
     */
    public synchronized void syncUpdatePriority(Long paperId, double newScore) {
        priorityHeap.updatePriority(paperId, newScore);
        PaperDTO paper = paperIndex.search(paperId);
        if (paper != null) {
            paper.setPriorityScore(newScore);
        }
    }

    /**
     * Synchronizes adding a new category into the N-Ary tree.
     */
    public synchronized void syncAddCategory(CategoryDTO cat) {
        NAryNode node = new NAryNode(cat.getId(), cat.getName(), cat.getType());
        String parent = cat.getParentId() != null ? cat.getParentId() : hierarchyTree.getRoot().getId();
        hierarchyTree.insert(parent, node);
    }

    /**
     * Synchronizes moving a category in the N-Ary tree.
     */
    public synchronized void syncMoveCategory(String nodeId, String newParentId) {
        hierarchyTree.move(nodeId, newParentId);
    }

    /**
     * Synchronizes deleting a category and its papers.
     */
    public synchronized List<String> syncDeleteCategory(String categoryId) {
        return hierarchyTree.delete(categoryId);
    }

    private void indexPaperInTrie(PaperDTO paper) {
        Long id = paper.getId();
        if (paper.getTitle() != null && !paper.getTitle().isBlank()) {
            // Full title
            searchTrie.insert(paper.getTitle(), id);
            // Title tokens (length >= 3)
            String[] tokens = paper.getTitle().split("[\\s,\\-_]+");
            for (String token : tokens) {
                if (token.length() >= 3) {
                    searchTrie.insert(token, id);
                }
            }
        }

        // Keywords
        if (paper.getKeywords() != null && !paper.getKeywords().isBlank()) {
            String[] kwList = paper.getKeywords().split(",");
            for (String kw : kwList) {
                String clean = kw.trim();
                if (!clean.isEmpty()) {
                    searchTrie.insert(clean, id);
                }
            }
        }

        // Authors
        if (paper.getAuthors() != null && !paper.getAuthors().isBlank()) {
            String[] authors = paper.getAuthors().split("[,;]+");
            for (String a : authors) {
                String clean = a.trim();
                if (!clean.isEmpty()) {
                    searchTrie.insert(clean, id);
                }
            }
        }
    }

    private void unindexPaperFromTrie(PaperDTO paper) {
        Long id = paper.getId();
        if (paper.getTitle() != null) {
            searchTrie.delete(paper.getTitle(), id);
            String[] tokens = paper.getTitle().split("[\\s,\\-_]+");
            for (String token : tokens) {
                if (token.length() >= 3) {
                    searchTrie.delete(token, id);
                }
            }
        }

        if (paper.getKeywords() != null) {
            String[] kwList = paper.getKeywords().split(",");
            for (String kw : kwList) {
                searchTrie.delete(kw.trim(), id);
            }
        }
    }

    public static PaperDTO toDTO(PaperEntity entity, String categoryName) {
        PaperDTO dto = new PaperDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setAuthors(entity.getAuthors());
        dto.setPaperAbstract(entity.getPaperAbstract());
        dto.setPublicationYear(entity.getPublicationYear());
        dto.setJournalOrConference(entity.getJournalOrConference());
        dto.setDoi(entity.getDoi());
        dto.setPriorityScore(entity.getPriorityScore());
        dto.setCategoryId(entity.getCategoryId());
        dto.setCategoryName(categoryName);
        dto.setKeywords(entity.getKeywords());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
