package com.hkms.controller;

import com.hkms.service.DsaSyncManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    private final DsaSyncManager syncManager;

    public RootController(DsaSyncManager syncManager) {
        this.syncManager = syncManager;
    }

    /**
     * Root endpoint returning a friendly system status and API directory.
     */
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> rootLandingPage() {
        int naryCount = syncManager.getHierarchyTree().size();
        int avlCount = syncManager.getPaperIndex().size();
        int trieWords = syncManager.getSearchTrie().getWordCount();
        int heapCount = syncManager.getPriorityHeap().size();

        String html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>HKMS Backend - Status & API Directory</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                        background: #f8fafc;
                        color: #1e293b;
                        margin: 0;
                        padding: 40px 20px;
                        display: flex;
                        justify-content: center;
                    }
                    .card {
                        background: #ffffff;
                        max-width: 680px;
                        width: 100%;
                        border-radius: 16px;
                        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
                        border: 1px solid #e2e8f0;
                        padding: 32px;
                    }
                    .badge {
                        display: inline-flex;
                        align-items: center;
                        gap: 6px;
                        background: #ecfdf5;
                        color: #065f46;
                        border: 1px solid #a7f3d0;
                        padding: 4px 12px;
                        border-radius: 9999px;
                        font-size: 12px;
                        font-weight: 600;
                    }
                    .dot {
                        width: 8px;
                        height: 8px;
                        background: #10b981;
                        border-radius: 50%;
                    }
                    h1 {
                        font-size: 22px;
                        margin: 16px 0 8px 0;
                        color: #0f172a;
                    }
                    p {
                        color: #64748b;
                        font-size: 14px;
                        line-height: 1.5;
                        margin: 0 0 24px 0;
                    }
                    .stats-grid {
                        display: grid;
                        grid-template-columns: repeat(2, 1fr);
                        gap: 12px;
                        margin-bottom: 24px;
                    }
                    .stat-box {
                        background: #f1f5f9;
                        border-radius: 10px;
                        padding: 12px 16px;
                    }
                    .stat-label {
                        font-size: 11px;
                        font-weight: 600;
                        text-transform: uppercase;
                        color: #64748b;
                    }
                    .stat-val {
                        font-size: 20px;
                        font-weight: 700;
                        color: #0f172a;
                        margin-top: 4px;
                    }
                    a.btn {
                        display: inline-block;
                        text-align: center;
                        background: #0284c7;
                        color: white;
                        text-decoration: none;
                        padding: 10px 18px;
                        border-radius: 8px;
                        font-weight: 600;
                        font-size: 14px;
                        margin-bottom: 16px;
                    }
                    a.btn:hover { background: #0369a1; }
                    .api-list {
                        font-size: 13px;
                        background: #f8fafc;
                        border: 1px solid #e2e8f0;
                        border-radius: 8px;
                        padding: 12px 16px;
                    }
                    .api-list a {
                        color: #0284c7;
                        text-decoration: none;
                        font-family: monospace;
                        font-weight: 600;
                    }
                    .api-list a:hover { text-decoration: underline; }
                </style>
            </head>
            <body>
                <div class="card">
                    <span class="badge"><span class="dot"></span> Backend Server Live & Operational</span>
                    <h1>Hierarchical Knowledge Management System (HKMS)</h1>
                    <p>Spring Boot 3.3.5 on Java 21 • SQLite Database Connected • Custom In-Memory DSA Engine Active</p>

                    <div class="stats-grid">
                        <div class="stat-box">
                            <div class="stat-label">N-Ary Tree Nodes</div>
                            <div class="stat-val">{NARY_COUNT}</div>
                        </div>
                        <div class="stat-box">
                            <div class="stat-label">AVL Tree Papers</div>
                            <div class="stat-val">{AVL_COUNT}</div>
                        </div>
                        <div class="stat-box">
                            <div class="stat-label">Trie Prefix Words</div>
                            <div class="stat-val">{TRIE_COUNT}</div>
                        </div>
                        <div class="stat-box">
                            <div class="stat-label">Max Heap Ranked Papers</div>
                            <div class="stat-val">{HEAP_COUNT}</div>
                        </div>
                    </div>

                    <a href="http://localhost:5173" class="btn" target="_blank">Open React Web Frontend (http://localhost:5173) &rarr;</a>

                    <div class="api-list">
                        <strong>Live REST API Endpoints:</strong>
                        <ul style="margin: 8px 0 0 0; padding-left: 20px; line-height: 1.8;">
                            <li><a href="/api/visualizer/summary">/api/visualizer/summary</a> - In-Memory DSA Status</li>
                            <li><a href="/api/papers">/api/papers</a> - All Papers (AVL In-Order)</li>
                            <li><a href="/api/hierarchy">/api/hierarchy</a> - N-Ary Category Tree</li>
                            <li><a href="/api/hierarchy/react-flow">/api/hierarchy/react-flow</a> - React Flow Nodes & Edges</li>
                            <li><a href="/api/ranking/top">/api/ranking/top</a> - Max Heap Top Ranked Papers</li>
                            <li><a href="/api/search/autocomplete?query=trans">/api/search/autocomplete?query=trans</a> - Prefix Trie Search</li>
                            <li><a href="/api/visualizer/avl">/api/visualizer/avl</a> - AVL Rotations & Tree Map</li>
                            <li><a href="/api/visualizer/heap">/api/visualizer/heap</a> - Heap 1D Memory Array View</li>
                        </ul>
                    </div>
                </div>
            </body>
            </html>
            """
            .replace("{NARY_COUNT}", String.valueOf(naryCount))
            .replace("{AVL_COUNT}", String.valueOf(avlCount))
            .replace("{TRIE_COUNT}", String.valueOf(trieWords))
            .replace("{HEAP_COUNT}", String.valueOf(heapCount));

        return ResponseEntity.ok(html);
    }

    @GetMapping(value = "/api/status")
    public ResponseEntity<Map<String, Object>> apiStatus() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "framework", "Spring Boot 3.3.5",
                "javaVersion", System.getProperty("java.version"),
                "naryNodes", syncManager.getHierarchyTree().size(),
                "avlPapers", syncManager.getPaperIndex().size(),
                "trieWords", syncManager.getSearchTrie().getWordCount(),
                "heapItems", syncManager.getPriorityHeap().size()
        ));
    }
}
