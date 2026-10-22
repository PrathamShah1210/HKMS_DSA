package com.hkms.controller;

import com.hkms.core.avl.AvlNode;
import com.hkms.core.avl.RotationEvent;
import com.hkms.core.heap.HeapItem;
import com.hkms.core.heap.HeapStepTrace;
import com.hkms.model.dto.PaperDTO;
import com.hkms.service.DsaSyncManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/visualizer")
public class VisualizerController {

    private final DsaSyncManager syncManager;

    public VisualizerController(DsaSyncManager syncManager) {
        this.syncManager = syncManager;
    }

    /**
     * Overall DSA status and audit metrics.
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> summary = new HashMap<>();

        // N-Ary Tree
        summary.put("naryTotalNodes", syncManager.getHierarchyTree().size());
        summary.put("naryTreeDepth", syncManager.getHierarchyTree().getDepth());

        // AVL Tree
        summary.put("avlPaperCount", syncManager.getPaperIndex().size());
        summary.put("avlTreeHeight", syncManager.getPaperIndex().getHeight());
        summary.put("avlIsValid", syncManager.getPaperIndex().isAvlTreeValid());
        summary.put("avlTotalRotations", syncManager.getPaperIndex().getRotationHistory().size());

        // Trie
        summary.put("trieTotalWords", syncManager.getSearchTrie().getWordCount());

        // Max Heap
        summary.put("heapTotalItems", syncManager.getPriorityHeap().size());
        summary.put("heapIsValid", syncManager.getPriorityHeap().isHeapValid());
        if (!syncManager.getPriorityHeap().isEmpty()) {
            summary.put("heapTopPaper", syncManager.getPriorityHeap().peekMax().getPayload());
        }

        return ResponseEntity.ok(summary);
    }

    /**
     * AVL Tree visual structure with balance factors and rotation trace.
     */
    @GetMapping("/avl")
    public ResponseEntity<Map<String, Object>> getAvlVisualization() {
        AvlNode<Long, PaperDTO> root = syncManager.getPaperIndex().getRoot();

        Map<String, Object> response = new HashMap<>();
        response.put("tree", convertAvlNodeToMap(root));
        response.put("height", syncManager.getPaperIndex().getHeight());
        response.put("size", syncManager.getPaperIndex().size());
        response.put("isValid", syncManager.getPaperIndex().isAvlTreeValid());
        response.put("rotationHistory", syncManager.getPaperIndex().getRotationHistory());

        return ResponseEntity.ok(response);
    }

    private Map<String, Object> convertAvlNodeToMap(AvlNode<Long, PaperDTO> node) {
        if (node == null) return null;

        Map<String, Object> map = new HashMap<>();
        map.put("key", node.getKey());
        map.put("title", node.getValue() != null ? node.getValue().getTitle() : "");
        map.put("height", node.getHeight());
        map.put("balanceFactor", node.getBalanceFactor());
        map.put("priorityScore", node.getValue() != null ? node.getValue().getPriorityScore() : 0.0);
        map.put("left", convertAvlNodeToMap(node.getLeft()));
        map.put("right", convertAvlNodeToMap(node.getRight()));

        return map;
    }

    /**
     * Max Heap 1D array representation and step trace history.
     */
    @GetMapping("/heap")
    public ResponseEntity<Map<String, Object>> getHeapVisualization() {
        List<HeapItem<PaperDTO>> elements = syncManager.getPriorityHeap().getElements();
        List<Map<String, Object>> arrayView = new ArrayList<>();

        for (int i = 0; i < elements.size(); i++) {
            HeapItem<PaperDTO> item = elements.get(i);
            Map<String, Object> node = new HashMap<>();
            node.put("index", i);
            node.put("id", item.getId());
            node.put("priority", item.getPriority());
            node.put("title", item.getPayload() != null ? item.getPayload().getTitle() : "");
            node.put("parentIndex", i > 0 ? (i - 1) / 2 : null);
            node.put("leftIndex", 2 * i + 1 < elements.size() ? 2 * i + 1 : null);
            node.put("rightIndex", 2 * i + 2 < elements.size() ? 2 * i + 2 : null);
            arrayView.add(node);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("elements", arrayView);
        response.put("size", elements.size());
        response.put("isValid", syncManager.getPriorityHeap().isHeapValid());
        response.put("traceHistory", syncManager.getPriorityHeap().getTraceHistory());

        return ResponseEntity.ok(response);
    }

    /**
     * Algorithmic Time & Space Complexity Matrix for academic evaluation.
     */
    @GetMapping("/complexities")
    public ResponseEntity<List<Map<String, String>>> getComplexities() {
        List<Map<String, String>> list = List.of(
                Map.of(
                        "dataStructure", "N-Ary Tree",
                        "purpose", "Multiway subject/topic hierarchy and paper reference binding",
                        "searchTime", "O(N) traversal or O(1) indexed",
                        "insertTime", "O(1) with parent reference",
                        "deleteTime", "O(K) where K is subtree size",
                        "spaceComplexity", "O(N) where N is total nodes",
                        "keyFeature", "Acyclic validation during reparenting; DFS/BFS traversals"
                ),
                Map.of(
                        "dataStructure", "AVL Tree",
                        "purpose", "Self-balancing binary search tree indexing papers by ID",
                        "searchTime", "O(log N)",
                        "insertTime", "O(log N) with rebalance",
                        "deleteTime", "O(log N) with rebalance",
                        "spaceComplexity", "O(N)",
                        "keyFeature", "LL, RR, LR, RL rotations strictly maintaining |BF| <= 1"
                ),
                Map.of(
                        "dataStructure", "Prefix Trie",
                        "purpose", "Real-time autocomplete and research term prefix searching",
                        "searchTime", "O(L) where L is query prefix length",
                        "insertTime", "O(L)",
                        "deleteTime", "O(L) with recursive branch pruning",
                        "spaceComplexity", "O(Σ · L · N) alphabet tree",
                        "keyFeature", "Dynamic branch pruning when paper references reach zero"
                ),
                Map.of(
                        "dataStructure", "Max Heap",
                        "purpose", "Priority ranking and top-K paper scheduling",
                        "searchTime", "O(1) peekMax; O(1) lookup via idToIndexMap",
                        "insertTime", "O(log N) sift-up",
                        "deleteTime", "O(log N) sift-down",
                        "spaceComplexity", "O(N) contiguous array",
                        "keyFeature", "Auxiliary map for O(log N) priority updates without O(N) scan"
                )
        );

        return ResponseEntity.ok(list);
    }
}
