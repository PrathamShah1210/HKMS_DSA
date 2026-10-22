package com.hkms.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PriorityUpdateRequest {

    @NotNull(message = "Priority score is required")
    @Min(value = 0, message = "Priority score must be at least 0")
    @Max(value = 100, message = "Priority score cannot exceed 100")
    private Double priorityScore;

    public PriorityUpdateRequest() {
    }

    public PriorityUpdateRequest(Double priorityScore) {
        this.priorityScore = priorityScore;
    }

    public Double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(Double priorityScore) {
        this.priorityScore = priorityScore;
    }
}
