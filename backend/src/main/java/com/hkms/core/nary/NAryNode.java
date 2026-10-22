package com.hkms.core.nary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An individual node within an N-Ary hierarchical tree.
 * A node can have any number of child nodes.
 */
public class NAryNode {

    private String id;
    private String name;
    private NodeType type;
    private String parentId;
    private Long paperId; // Non-null if type == NodeType.PAPER
    private final List<NAryNode> children;

    public NAryNode() {
        this.children = new ArrayList<>();
    }

    public NAryNode(String id, String name, NodeType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.children = new ArrayList<>();
    }

    public NAryNode(String id, String name, NodeType type, Long paperId) {
        this(id, name, type);
        this.paperId = paperId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public List<NAryNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(NAryNode child) {
        if (child != null && !children.contains(child)) {
            children.add(child);
            child.setParentId(this.id);
        }
    }

    public boolean removeChild(NAryNode child) {
        return children.remove(child);
    }

    public boolean removeChildById(String childId) {
        return children.removeIf(child -> child.getId().equals(childId));
    }

    public int getChildCount() {
        return children.size();
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public String toString() {
        return "NAryNode{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", children=" + children.size() +
                '}';
    }
}
