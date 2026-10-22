package com.hkms.controller;

import com.hkms.core.nary.NAryNode;
import com.hkms.core.nary.NodeType;
import com.hkms.core.nary.TraversalResult;
import com.hkms.model.dto.CategoryDTO;
import com.hkms.model.dto.HierarchyMoveRequest;
import com.hkms.model.entity.CategoryEntity;
import com.hkms.repository.CategoryRepository;
import com.hkms.service.DsaSyncManager;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/hierarchy")
public class HierarchyController {

    private final DsaSyncManager syncManager;
    private final CategoryRepository categoryRepository;

    public HierarchyController(DsaSyncManager syncManager, CategoryRepository categoryRepository) {
        this.syncManager = syncManager;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Returns nested hierarchy representation of the N-Ary tree.
     */
    @GetMapping
    public ResponseEntity<CategoryDTO> getHierarchy() {
        NAryNode root = syncManager.getHierarchyTree().getRoot();
        if (root == null) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(convertToDTO(root));
    }

    private CategoryDTO convertToDTO(NAryNode node) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(node.getId());
        dto.setName(node.getName());
        dto.setType(node.getType());
        dto.setParentId(node.getParentId());
        dto.setPaperCount(syncManager.getHierarchyTree().getSubtreePaperIds(node.getId()).size());

        for (NAryNode child : node.getChildren()) {
            if (child.getType() != NodeType.PAPER) {
                dto.getChildren().add(convertToDTO(child));
            }
        }
        return dto;
    }

    /**
     * Returns nodes and edges formatted specifically for React Flow graph rendering.
     */
    @GetMapping("/react-flow")
    public ResponseEntity<Map<String, Object>> getReactFlowGraph() {
        NAryNode root = syncManager.getHierarchyTree().getRoot();
        List<Map<String, Object>> flowNodes = new ArrayList<>();
        List<Map<String, Object>> flowEdges = new ArrayList<>();

        if (root != null) {
            buildReactFlowData(root, flowNodes, flowEdges, 0, 0, 300);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("nodes", flowNodes);
        response.put("edges", flowEdges);
        return ResponseEntity.ok(response);
    }

    private int buildReactFlowData(NAryNode node, List<Map<String, Object>> nodes,
                                  List<Map<String, Object>> edges, int level, int xOffset, int siblingSpacing) {
        Map<String, Object> flowNode = new HashMap<>();
        flowNode.put("id", node.getId());
        flowNode.put("type", "customTree");

        Map<String, Object> position = new HashMap<>();
        position.put("x", xOffset);
        position.put("y", level * 140);
        flowNode.put("position", position);

        Map<String, Object> data = new HashMap<>();
        data.put("id", node.getId());
        data.put("label", node.getName());
        data.put("type", node.getType().name());
        data.put("paperCount", syncManager.getHierarchyTree().getSubtreePaperIds(node.getId()).size());
        data.put("childCount", node.getChildCount());
        flowNode.put("data", data);

        nodes.add(flowNode);

        int currentX = xOffset - ((node.getChildren().size() - 1) * siblingSpacing) / 2;
        for (NAryNode child : node.getChildren()) {
            // Edge from node to child
            Map<String, Object> edge = new HashMap<>();
            edge.put("id", "e-" + node.getId() + "-" + child.getId());
            edge.put("source", node.getId());
            edge.put("target", child.getId());
            edge.put("animated", child.getType() == NodeType.PAPER);
            edges.add(edge);

            buildReactFlowData(child, nodes, edges, level + 1, currentX, siblingSpacing);
            currentX += siblingSpacing;
        }

        return xOffset;
    }

    /**
     * Creates a new category node and synchronizes with SQLite and N-Ary tree.
     */
    @PostMapping("/nodes")
    public ResponseEntity<CategoryDTO> addCategoryNode(@Valid @RequestBody CategoryDTO request) {
        if (request.getId() == null || request.getId().isBlank()) {
            request.setId(UUID.randomUUID().toString());
        }

        CategoryEntity entity = new CategoryEntity(
                request.getId(), request.getName(), request.getType(),
                request.getParentId(), request.getSortOrder()
        );
        categoryRepository.save(entity);

        syncManager.syncAddCategory(request);
        return ResponseEntity.ok(request);
    }

    /**
     * Deletes a category node, cascades to SQLite, and synchronizes memory.
     */
    @DeleteMapping("/nodes/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategoryNode(@PathVariable String id) {
        if (id.equalsIgnoreCase("root")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot delete the root node"));
        }

        List<String> deletedIds = syncManager.syncDeleteCategory(id);
        for (String delId : deletedIds) {
            categoryRepository.deleteById(delId);
        }

        return ResponseEntity.ok(Map.of(
                "message", "Subtree deleted successfully",
                "deletedNodeIds", deletedIds
        ));
    }

    /**
     * Moves a node to a new parent with cycle detection.
     */
    @PostMapping("/move")
    public ResponseEntity<Map<String, Object>> moveCategoryNode(@Valid @RequestBody HierarchyMoveRequest request) {
        try {
            syncManager.syncMoveCategory(request.getNodeId(), request.getNewParentId());

            categoryRepository.findById(request.getNodeId()).ifPresent(cat -> {
                cat.setParentId(request.getNewParentId());
                categoryRepository.save(cat);
            });

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Node '" + request.getNodeId() + "' reparented under '" + request.getNewParentId() + "'"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Traversal endpoints for DSA academic demonstration.
     */
    @GetMapping("/traversals/dfs-pre")
    public ResponseEntity<TraversalResult> getDfsPreOrder() {
        return ResponseEntity.ok(syncManager.getHierarchyTree().traverseDFS(true));
    }

    @GetMapping("/traversals/dfs-post")
    public ResponseEntity<TraversalResult> getDfsPostOrder() {
        return ResponseEntity.ok(syncManager.getHierarchyTree().traverseDFS(false));
    }

    @GetMapping("/traversals/bfs")
    public ResponseEntity<TraversalResult> getBfs() {
        return ResponseEntity.ok(syncManager.getHierarchyTree().traverseBFS());
    }
}
