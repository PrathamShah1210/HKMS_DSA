package com.hkms.core.nary;

import java.util.*;

/**
 * Custom Pure Java N-Ary Tree Implementation for the HKMS hierarchy.
 * Manages Subjects, Topics, Subtopics, and Paper references.
 *
 * Algorithm Features:
 * - Arbitrary branching factor per node
 * - Acyclic invariant validation during node move/reparenting
 * - Full Pre-order and Post-order Depth-First Search (DFS)
 * - Level-order Breadth-First Search (BFS) using a custom queue
 * - Fast index lookup for subtree extractions
 */
public class NAryTree {

    private NAryNode root;
    private final Map<String, NAryNode> idIndex;

    public NAryTree() {
        this.idIndex = new HashMap<>();
    }

    public NAryTree(NAryNode root) {
        this.idIndex = new HashMap<>();
        setRoot(root);
    }

    public synchronized NAryNode getRoot() {
        return root;
    }

    public synchronized void setRoot(NAryNode root) {
        this.root = root;
        idIndex.clear();
        if (root != null) {
            indexSubtree(root);
        }
    }

    private void indexSubtree(NAryNode node) {
        if (node == null) return;
        idIndex.put(node.getId(), node);
        for (NAryNode child : node.getChildren()) {
            indexSubtree(child);
        }
    }

    /**
     * Inserts a new node under the given parentId.
     * If parentId is null and tree has no root, the node becomes the root.
     */
    public synchronized boolean insert(String parentId, NAryNode newNode) {
        if (newNode == null || newNode.getId() == null) {
            throw new IllegalArgumentException("Node and Node ID cannot be null");
        }

        if (idIndex.containsKey(newNode.getId())) {
            throw new IllegalArgumentException("Duplicate node ID: " + newNode.getId());
        }

        if (root == null) {
            if (parentId == null) {
                setRoot(newNode);
                return true;
            } else {
                throw new IllegalStateException("Cannot insert under parentId '" + parentId + "' because tree is empty");
            }
        }

        if (parentId == null) {
            throw new IllegalArgumentException("Parent ID required for non-root insertions");
        }

        NAryNode parent = idIndex.get(parentId);
        if (parent == null) {
            throw new NoSuchElementException("Parent node with ID '" + parentId + "' not found");
        }

        parent.addChild(newNode);
        indexSubtree(newNode);
        return true;
    }

    /**
     * Deletes a node and its entire subtree from the hierarchy.
     * Returns a list of deleted node IDs (for cascading paper cleanup).
     */
    public synchronized List<String> delete(String nodeId) {
        if (nodeId == null || !idIndex.containsKey(nodeId)) {
            return Collections.emptyList();
        }

        NAryNode target = idIndex.get(nodeId);
        List<String> deletedIds = new ArrayList<>();

        if (target == root) {
            collectSubtreeIds(root, deletedIds);
            for (String id : deletedIds) {
                idIndex.remove(id);
            }
            root = null;
            return deletedIds;
        }

        NAryNode parent = idIndex.get(target.getParentId());
        if (parent != null) {
            parent.removeChildById(nodeId);
        }

        collectSubtreeIds(target, deletedIds);
        for (String id : deletedIds) {
            idIndex.remove(id);
        }

        return deletedIds;
    }

    private void collectSubtreeIds(NAryNode node, List<String> result) {
        if (node == null) return;
        result.add(node.getId());
        for (NAryNode child : node.getChildren()) {
            collectSubtreeIds(child, result);
        }
    }

    /**
     * Moves a node (and its entire subtree) under a new parent.
     * Enforces the Acyclic Invariant: newParent cannot be the node itself or any of its descendants.
     */
    public synchronized boolean move(String nodeId, String newParentId) {
        if (nodeId == null || newParentId == null) {
            throw new IllegalArgumentException("Node ID and new Parent ID must be specified");
        }

        if (nodeId.equals(newParentId)) {
            throw new IllegalArgumentException("Cannot move a node to be its own parent");
        }

        NAryNode target = idIndex.get(nodeId);
        if (target == null) {
            throw new NoSuchElementException("Node with ID '" + nodeId + "' not found");
        }

        if (target == root) {
            throw new UnsupportedOperationException("Root node cannot be moved under another parent");
        }

        NAryNode newParent = idIndex.get(newParentId);
        if (newParent == null) {
            throw new NoSuchElementException("New parent node with ID '" + newParentId + "' not found");
        }

        // Cycle Detection: Walk upwards from newParent to root
        NAryNode currentAncestor = newParent;
        while (currentAncestor != null) {
            if (currentAncestor.getId().equals(nodeId)) {
                throw new IllegalArgumentException("Cyclic relationship detected: Cannot move node '" +
                        nodeId + "' under its own descendant '" + newParentId + "'");
            }
            if (currentAncestor.getParentId() == null) break;
            currentAncestor = idIndex.get(currentAncestor.getParentId());
        }

        // Detach from current parent
        NAryNode oldParent = idIndex.get(target.getParentId());
        if (oldParent != null) {
            oldParent.removeChildById(nodeId);
        }

        // Re-attach under new parent
        newParent.addChild(target);
        return true;
    }

    /**
     * Depth-First Search (Pre-Order or Post-Order).
     */
    public synchronized TraversalResult traverseDFS(boolean preOrder) {
        TraversalResult result = new TraversalResult(preOrder ? "DFS_PRE_ORDER" : "DFS_POST_ORDER");
        if (root != null) {
            dfsRecursive(root, preOrder, result);
        }
        return result;
    }

    private void dfsRecursive(NAryNode node, boolean preOrder, TraversalResult result) {
        if (node == null) return;

        if (preOrder) {
            result.addStep(node);
        }

        for (NAryNode child : node.getChildren()) {
            dfsRecursive(child, preOrder, result);
        }

        if (!preOrder) {
            result.addStep(node);
        }
    }

    /**
     * Breadth-First Search (Level-Order) using an iterative queue.
     */
    public synchronized TraversalResult traverseBFS() {
        TraversalResult result = new TraversalResult("BFS_LEVEL_ORDER");
        if (root == null) return result;

        Queue<NAryNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            NAryNode current = queue.poll();
            result.addStep(current);

            for (NAryNode child : current.getChildren()) {
                queue.offer(child);
            }
        }

        return result;
    }

    /**
     * Searches for a node by ID in O(1) via the registered index.
     */
    public synchronized NAryNode findById(String nodeId) {
        return idIndex.get(nodeId);
    }

    /**
     * Collects all paper IDs contained within the subtree rooted at nodeId.
     */
    public synchronized List<Long> getSubtreePaperIds(String nodeId) {
        NAryNode startNode = idIndex.get(nodeId);
        if (startNode == null) return Collections.emptyList();

        List<Long> paperIds = new ArrayList<>();
        collectPaperIds(startNode, paperIds);
        return paperIds;
    }

    private void collectPaperIds(NAryNode node, List<Long> paperIds) {
        if (node.getPaperId() != null) {
            paperIds.add(node.getPaperId());
        }
        for (NAryNode child : node.getChildren()) {
            collectPaperIds(child, paperIds);
        }
    }

    public synchronized int size() {
        return idIndex.size();
    }

    public synchronized int getDepth() {
        return calculateDepth(root);
    }

    private int calculateDepth(NAryNode node) {
        if (node == null) return 0;
        int maxChildDepth = 0;
        for (NAryNode child : node.getChildren()) {
            maxChildDepth = Math.max(maxChildDepth, calculateDepth(child));
        }
        return 1 + maxChildDepth;
    }

    public synchronized void clear() {
        root = null;
        idIndex.clear();
    }
}
