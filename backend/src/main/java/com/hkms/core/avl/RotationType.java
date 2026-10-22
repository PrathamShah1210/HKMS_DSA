package com.hkms.core.avl;

/**
 * Enumeration of the four fundamental AVL rotation types.
 */
public enum RotationType {
    LEFT_LEFT("LL", "Single Right Rotation (Left-Left case)"),
    RIGHT_RIGHT("RR", "Single Left Rotation (Right-Right case)"),
    LEFT_RIGHT("LR", "Double Rotation: Left then Right (Left-Right case)"),
    RIGHT_LEFT("RL", "Double Rotation: Right then Left (Right-Left case)");

    private final String code;
    private final String description;

    RotationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
