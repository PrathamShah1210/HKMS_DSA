package com.hkms.core.heap;

/**
 * Step trace of heap operations and element swaps for live visual demonstration.
 */
public class HeapStepTrace {

    private final String operation;
    private final int index1;
    private final int index2;
    private final String description;

    public HeapStepTrace(String operation, int index1, int index2, String description) {
        this.operation = operation;
        this.index1 = index1;
        this.index2 = index2;
        this.description = description;
    }

    public String getOperation() {
        return operation;
    }

    public int getIndex1() {
        return index1;
    }

    public int getIndex2() {
        return index2;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "[" + operation + "] indices (" + index1 + " <-> " + index2 + "): " + description;
    }
}
