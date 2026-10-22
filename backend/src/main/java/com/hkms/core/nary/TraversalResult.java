package com.hkms.core.nary;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the output of a traversal operation across the N-Ary tree.
 */
public class TraversalResult {

    private final String traversalType;
    private final List<NAryNode> nodes;
    private final List<String> visitedOrder;

    public TraversalResult(String traversalType) {
        this.traversalType = traversalType;
        this.nodes = new ArrayList<>();
        this.visitedOrder = new ArrayList<>();
    }

    public void addStep(NAryNode node) {
        this.nodes.add(node);
        this.visitedOrder.add(node.getName() + " (" + node.getType() + ")");
    }

    public String getTraversalType() {
        return traversalType;
    }

    public List<NAryNode> getNodes() {
        return nodes;
    }

    public List<String> getVisitedOrder() {
        return visitedOrder;
    }

    public int size() {
        return nodes.size();
    }
}
