package com.hkms.model.entity;

import com.hkms.core.nary.NodeType;
import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NodeType type;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "sort_order")
    private Integer sortOrder;

    public CategoryEntity() {
    }

    public CategoryEntity(String id, String name, NodeType type, String parentId, Integer sortOrder) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parentId = parentId;
        this.sortOrder = sortOrder;
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

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
