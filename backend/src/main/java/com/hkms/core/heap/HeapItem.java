package com.hkms.core.heap;

/**
 * An item stored within the Max Heap.
 */
public class HeapItem<T> {

    private final Long id;
    private double priority;
    private T payload;

    public HeapItem(Long id, double priority, T payload) {
        this.id = id;
        this.priority = priority;
        this.payload = payload;
    }

    public Long getId() {
        return id;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "HeapItem{id=" + id + ", priority=" + priority + '}';
    }
}
