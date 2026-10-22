package com.hkms.controller;

import com.hkms.core.trie.AutocompleteMatch;
import com.hkms.model.dto.PaperDTO;
import com.hkms.service.DsaSyncManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final DsaSyncManager syncManager;

    public SearchController(DsaSyncManager syncManager) {
        this.syncManager = syncManager;
    }

    /**
     * Real-time prefix autocomplete powered by custom Trie.
     * Looks up matching words in the Trie, then retrieves matching papers
     * in O(log N) from the AVL Tree.
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<Map<String, Object>> autocomplete(
            @RequestParam String query,
            @RequestParam(defaultValue = "8") int limit) {

        long startTime = System.nanoTime();

        // 1. Query Trie for prefix matches
        List<AutocompleteMatch> matches = syncManager.getSearchTrie().autocomplete(query, limit);

        // 2. Fetch associated papers using AVL tree index
        Set<Long> uniquePaperIds = new LinkedHashSet<>();
        for (AutocompleteMatch match : matches) {
            uniquePaperIds.addAll(match.getPaperIds());
        }

        List<PaperDTO> papers = new ArrayList<>();
        for (Long id : uniquePaperIds) {
            PaperDTO paper = syncManager.getPaperIndex().search(id);
            if (paper != null) {
                papers.add(paper);
            }
        }

        long durationMicros = (System.nanoTime() - startTime) / 1000;

        Map<String, Object> response = new HashMap<>();
        response.put("query", query);
        response.put("executionTimeMicros", durationMicros);
        response.put("suggestions", matches);
        response.put("matchedPapers", papers);

        return ResponseEntity.ok(response);
    }

    /**
     * Exact term search in Trie.
     */
    @GetMapping("/exact")
    public ResponseEntity<Map<String, Object>> searchExact(@RequestParam String query) {
        boolean exists = syncManager.getSearchTrie().contains(query);
        List<AutocompleteMatch> matches = exists ? syncManager.getSearchTrie().searchPrefix(query) : Collections.emptyList();

        return ResponseEntity.ok(Map.of(
                "query", query,
                "exists", exists,
                "matches", matches
        ));
    }
}
