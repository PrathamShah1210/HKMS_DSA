package com.hkms.core.avl;

/**
 * Audit record capturing an AVL rotation event.
 * Used for live step-by-step DSA visualization and playback.
 */
public class RotationEvent {

    private final String timestamp;
    private final RotationType rotationType;
    private final Object pivotKey;
    private final int balanceFactorBefore;
    private final String explanation;

    public RotationEvent(RotationType rotationType, Object pivotKey, int balanceFactorBefore, String explanation) {
        this.timestamp = java.time.Instant.now().toString();
        this.rotationType = rotationType;
        this.pivotKey = pivotKey;
        this.balanceFactorBefore = balanceFactorBefore;
        this.explanation = explanation;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public RotationType getRotationType() {
        return rotationType;
    }

    public Object getPivotKey() {
        return pivotKey;
    }

    public int getBalanceFactorBefore() {
        return balanceFactorBefore;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        return "[" + rotationType.getCode() + "] Pivot: " + pivotKey + " (BF: " + balanceFactorBefore + ") - " + explanation;
    }
}
