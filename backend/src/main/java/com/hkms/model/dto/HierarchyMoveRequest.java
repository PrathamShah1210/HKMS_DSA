package com.hkms.model.dto;

import jakarta.validation.constraints.NotBlank;

public class HierarchyMoveRequest {

    @NotBlank(message = "Node ID to move is required")
    private String nodeId;

    @NotBlank(message = "Target parent ID is required")
    private String newParentId;

    public HierarchyMoveRequest() {
    }

    public HierarchyMoveRequest(String nodeId, String newParentId) {
        this.nodeId = nodeId;
        this.newParentId = newParentId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNewParentId() {
        return newParentId;
    }

    public void setNewParentId(String newParentId) {
        this.newParentId = newParentId;
    }
}
