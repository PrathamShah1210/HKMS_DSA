package com.hkms.controller;

import com.hkms.core.heap.HeapItem;
import com.hkms.model.dto.PaperDTO;
import com.hkms.model.dto.PriorityUpdateRequest;
import com.hkms.repository.PaperRepository;
import com.hkms.service.DsaSyncManager;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final DsaSyncManager syncManager;
    private final PaperRepository paperRepository;

    public RankingController(DsaSyncManager syncManager, PaperRepository paperRepository) {
        this.syncManager = syncManager;
        this.paperRepository = paperRepository;
    }

    /**
     * Retrieves top-K ranked research papers from the Max Heap without destroying it.
     */
    @GetMapping("/top")
    public ResponseEntity<List<HeapItem<PaperDTO>>> getTopRanked(@RequestParam(defaultValue = "10") int limit) {
        List<HeapItem<PaperDTO>> topK = syncManager.getPriorityHeap().getTopK(limit);
        return ResponseEntity.ok(topK);
    }

    /**
     * O(1) peek of the highest priority paper.
     */
    @GetMapping("/peek")
    public ResponseEntity<HeapItem<PaperDTO>> peekMax() {
        if (syncManager.getPriorityHeap().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(syncManager.getPriorityHeap().peekMax());
    }

    /**
     * Extracts (pops) the maximum priority paper from the heap in O(log N).
     */
    @PostMapping("/extract-max")
    public ResponseEntity<HeapItem<PaperDTO>> extractMax() {
        if (syncManager.getPriorityHeap().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        HeapItem<PaperDTO> max = syncManager.getPriorityHeap().extractMax();
        return ResponseEntity.ok(max);
    }

    /**
     * Re-inserts a previously popped paper back into the Max Heap.
     */
    @PostMapping("/{id}/reinsert")
    public ResponseEntity<Map<String, Object>> reinsertPaper(
            @PathVariable Long id,
            @RequestParam(required = false) Double priority) {

        PaperDTO paper = syncManager.getPaperIndex().search(id);
        if (paper == null) {
            var entityOpt = paperRepository.findById(id);
            if (entityOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            paper = DsaSyncManager.toDTO(entityOpt.get(), "");
        }

        double score = (priority != null) ? priority : paper.getPriorityScore();
        syncManager.getPriorityHeap().insert(id, score, paper);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Paper #" + id + " re-inserted into Max Heap with priority " + score,
                "heapSize", syncManager.getPriorityHeap().size(),
                "heapValid", syncManager.getPriorityHeap().isHeapValid()
        ));
    }

    /**
     * Modifies paper priority in O(log N) using the internal index map,
     * and persists the update to SQLite.
     */
    @PatchMapping("/{id}/priority")
    public ResponseEntity<Map<String, Object>> updatePriority(
            @PathVariable Long id,
            @Valid @RequestBody PriorityUpdateRequest request) {

        boolean success = syncManager.getPriorityHeap().updatePriority(id, request.getPriorityScore());
        if (!success) {
            return ResponseEntity.notFound().build();
        }

        // Update in SQLite
        paperRepository.findById(id).ifPresent(p -> {
            p.setPriorityScore(request.getPriorityScore());
            paperRepository.save(p);
        });

        // Update in AVL Tree
        PaperDTO avlPaper = syncManager.getPaperIndex().search(id);
        if (avlPaper != null) {
            avlPaper.setPriorityScore(request.getPriorityScore());
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "paperId", id,
                "newPriority", request.getPriorityScore(),
                "heapValid", syncManager.getPriorityHeap().isHeapValid()
        ));
    }
}
