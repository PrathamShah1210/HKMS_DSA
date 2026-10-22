package com.hkms.controller;

import com.hkms.model.dto.PaperDTO;
import com.hkms.model.entity.CategoryEntity;
import com.hkms.model.entity.PaperEntity;
import com.hkms.repository.CategoryRepository;
import com.hkms.repository.PaperRepository;
import com.hkms.service.DsaSyncManager;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/papers")
public class PaperController {

    private final DsaSyncManager syncManager;
    private final PaperRepository paperRepository;
    private final CategoryRepository categoryRepository;

    public PaperController(DsaSyncManager syncManager, PaperRepository paperRepository, CategoryRepository categoryRepository) {
        this.syncManager = syncManager;
        this.paperRepository = paperRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Lists all papers from AVL Tree index (or SQLite).
     */
    @GetMapping
    public ResponseEntity<List<PaperDTO>> getAllPapers() {
        List<PaperDTO> papers = syncManager.getPaperIndex().inOrderTraversal();
        return ResponseEntity.ok(papers);
    }

    /**
     * Retrieves paper by ID in O(log N) using the in-memory AVL Tree index.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaperDTO> getPaperById(@PathVariable Long id) {
        // Look up directly in AVL Tree
        PaperDTO paper = syncManager.getPaperIndex().search(id);
        if (paper == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(paper);
    }

    /**
     * Retrieves all papers attached under a given category or its subtopics.
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PaperDTO>> getPapersByCategory(@PathVariable String categoryId) {
        List<Long> paperIds = syncManager.getHierarchyTree().getSubtreePaperIds(categoryId);
        List<PaperDTO> papers = new ArrayList<>();
        for (Long id : paperIds) {
            PaperDTO p = syncManager.getPaperIndex().search(id);
            if (p != null) {
                papers.add(p);
            }
        }
        return ResponseEntity.ok(papers);
    }

    /**
     * Creates a new paper, persists to SQLite, and synchronizes with AVL, Trie, Heap, and N-Ary Tree.
     */
    @PostMapping
    public ResponseEntity<PaperDTO> createPaper(@Valid @RequestBody PaperDTO request) {
        PaperEntity entity = new PaperEntity();
        entity.setTitle(request.getTitle());
        entity.setAuthors(request.getAuthors());
        entity.setPaperAbstract(request.getPaperAbstract());
        entity.setPublicationYear(request.getPublicationYear());
        entity.setJournalOrConference(request.getJournalOrConference());
        entity.setDoi(request.getDoi());
        entity.setPriorityScore(request.getPriorityScore());
        entity.setCategoryId(request.getCategoryId());
        entity.setKeywords(request.getKeywords());

        PaperEntity saved = paperRepository.save(entity);

        String catName = categoryRepository.findById(saved.getCategoryId())
                .map(CategoryEntity::getName)
                .orElse("Uncategorized");

        PaperDTO dto = DsaSyncManager.toDTO(saved, catName);
        syncManager.syncAddPaper(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Updates an existing paper and synchronizes across all data structures.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PaperDTO> updatePaper(@PathVariable Long id, @Valid @RequestBody PaperDTO request) {
        Optional<PaperEntity> existingOpt = paperRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        PaperEntity existing = existingOpt.get();
        PaperDTO oldDto = syncManager.getPaperIndex().search(id);
        if (oldDto == null) {
            oldDto = DsaSyncManager.toDTO(existing, "");
        }

        existing.setTitle(request.getTitle());
        existing.setAuthors(request.getAuthors());
        existing.setPaperAbstract(request.getPaperAbstract());
        existing.setPublicationYear(request.getPublicationYear());
        existing.setJournalOrConference(request.getJournalOrConference());
        existing.setDoi(request.getDoi());
        existing.setPriorityScore(request.getPriorityScore());
        existing.setCategoryId(request.getCategoryId());
        existing.setKeywords(request.getKeywords());

        PaperEntity updated = paperRepository.save(existing);
        String catName = categoryRepository.findById(updated.getCategoryId())
                .map(CategoryEntity::getName)
                .orElse("Uncategorized");

        PaperDTO newDto = DsaSyncManager.toDTO(updated, catName);
        syncManager.syncUpdatePaper(oldDto, newDto);

        return ResponseEntity.ok(newDto);
    }

    /**
     * Deletes a paper from SQLite and unindexes from all data structures.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePaper(@PathVariable Long id) {
        Optional<PaperEntity> existingOpt = paperRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        PaperEntity existing = existingOpt.get();
        paperRepository.deleteById(id);
        syncManager.syncDeletePaper(id, existing.getCategoryId(), existing.getTitle(), existing.getKeywords());

        return ResponseEntity.ok(Map.of("message", "Paper " + id + " deleted successfully"));
    }
}
