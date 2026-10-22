package com.hkms.core.heap;

import java.util.*;

/**
 * Custom Pure Java Binary Max-Heap Implementation.
 * Used for dynamic research paper ranking by explicit priority scores.
 *
 * Algorithm Features:
 * - Contiguous array representation of a complete binary tree
 * - Parent: (i - 1) / 2, Left: 2i + 1, Right: 2i + 2
 * - O(1) peekMax
 * - O(log N) insert and extractMax
 * - O(log N) priority updates enabled by an auxiliary idToIndexMap (avoiding O(N) linear search)
 * - Step-by-step trace generation for algorithm visualization
 */
public class MaxHeap<T> {

    private final List<HeapItem<T>> elements;
    private final Map<Long, Integer> idToIndexMap;
    private final List<HeapStepTrace> traceHistory;

    public MaxHeap() {
        this.elements = new ArrayList<>();
        this.idToIndexMap = new HashMap<>();
        this.traceHistory = new ArrayList<>();
    }

    public synchronized int size() {
        return elements.size();
    }

    public synchronized boolean isEmpty() {
        return elements.isEmpty();
    }

    public synchronized List<HeapItem<T>> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public synchronized List<HeapStepTrace> getTraceHistory() {
        return Collections.unmodifiableList(traceHistory);
    }

    public synchronized void clearTraceHistory() {
        traceHistory.clear();
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int leftChild(int i) {
        return 2 * i + 1;
    }

    private int rightChild(int i) {
        return 2 * i + 2;
    }

    private void swap(int i, int j, String context) {
        HeapItem<T> itemI = elements.get(i);
        HeapItem<T> itemJ = elements.get(j);

        elements.set(i, itemJ);
        elements.set(j, itemI);

        idToIndexMap.put(itemJ.getId(), i);
        idToIndexMap.put(itemI.getId(), j);

        traceHistory.add(new HeapStepTrace(
                context, i, j,
                "Swapped element " + itemI.getId() + " (P: " + itemI.getPriority() + ") with " +
                        itemJ.getId() + " (P: " + itemJ.getPriority() + ")"
        ));
    }

    /**
     * Inserts an item into the max heap in O(log N).
     */
    public synchronized void insert(Long id, double priority, T payload) {
        if (id == null) {
            throw new IllegalArgumentException("Heap item ID cannot be null");
        }

        if (idToIndexMap.containsKey(id)) {
            // Already present: update priority instead
            updatePriority(id, priority);
            return;
        }

        HeapItem<T> item = new HeapItem<>(id, priority, payload);
        elements.add(item);
        int currentIndex = elements.size() - 1;
        idToIndexMap.put(id, currentIndex);

        traceHistory.add(new HeapStepTrace(
                "INSERT", currentIndex, currentIndex,
                "Inserted item " + id + " at end index " + currentIndex + " with priority " + priority
        ));

        siftUp(currentIndex);
    }

    /**
     * Restores heap invariant upward from the given index.
     */
    private void siftUp(int index) {
        int current = index;
        while (current > 0) {
            int p = parent(current);
            if (elements.get(current).getPriority() > elements.get(p).getPriority()) {
                swap(current, p, "SIFT_UP");
                current = p;
            } else {
                break;
            }
        }
    }

    /**
     * Peeks the highest priority item in O(1).
     */
    public synchronized HeapItem<T> peekMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        return elements.get(0);
    }

    /**
     * Extracts the highest priority item in O(log N).
     */
    public synchronized HeapItem<T> extractMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }

        HeapItem<T> maxItem = elements.get(0);
        int lastIndex = elements.size() - 1;

        if (elements.size() == 1) {
            elements.remove(0);
            idToIndexMap.remove(maxItem.getId());
            return maxItem;
        }

        // Move last element to root and remove last
        HeapItem<T> lastItem = elements.remove(lastIndex);
        idToIndexMap.remove(maxItem.getId());

        elements.set(0, lastItem);
        idToIndexMap.put(lastItem.getId(), 0);

        traceHistory.add(new HeapStepTrace(
                "EXTRACT_MAX", 0, lastIndex,
                "Replaced root " + maxItem.getId() + " with last element " + lastItem.getId()
        ));

        siftDown(0);
        return maxItem;
    }

    /**
     * Restores heap invariant downward from the given index.
     */
    private void siftDown(int index) {
        int current = index;
        int size = elements.size();

        while (true) {
            int left = leftChild(current);
            int right = rightChild(current);
            int largest = current;

            if (left < size && elements.get(left).getPriority() > elements.get(largest).getPriority()) {
                largest = left;
            }

            if (right < size && elements.get(right).getPriority() > elements.get(largest).getPriority()) {
                largest = right;
            }

            if (largest != current) {
                swap(current, largest, "SIFT_DOWN");
                current = largest;
            } else {
                break;
            }
        }
    }

    /**
     * Updates the priority score of an element in O(log N) using the index map.
     */
    public synchronized boolean updatePriority(Long id, double newPriority) {
        if (id == null || !idToIndexMap.containsKey(id)) {
            return false;
        }

        int index = idToIndexMap.get(id);
        HeapItem<T> item = elements.get(index);
        double oldPriority = item.getPriority();
        item.setPriority(newPriority);

        traceHistory.add(new HeapStepTrace(
                "UPDATE_PRIORITY", index, index,
                "Updated item " + id + " priority from " + oldPriority + " to " + newPriority
        ));

        if (newPriority > oldPriority) {
            siftUp(index);
        } else if (newPriority < oldPriority) {
            siftDown(index);
        }

        return true;
    }

    /**
     * Deletes an arbitrary item by ID in O(log N).
     */
    public synchronized boolean delete(Long id) {
        if (id == null || !idToIndexMap.containsKey(id)) {
            return false;
        }

        int index = idToIndexMap.get(id);
        int lastIndex = elements.size() - 1;

        if (index == lastIndex) {
            elements.remove(lastIndex);
            idToIndexMap.remove(id);
            return true;
        }

        HeapItem<T> lastItem = elements.remove(lastIndex);
        idToIndexMap.remove(id);

        elements.set(index, lastItem);
        idToIndexMap.put(lastItem.getId(), index);

        // May need either siftUp or siftDown depending on new element compared to neighbors
        siftUp(index);
        siftDown(index);
        return true;
    }

    /**
     * Retrieves the top K items without permanently destroying the heap.
     */
    public synchronized List<HeapItem<T>> getTopK(int k) {
        if (k <= 0 || isEmpty()) {
            return Collections.emptyList();
        }

        int limit = Math.min(k, elements.size());
        // Clone internal array to avoid destroying heap
        MaxHeap<T> clone = new MaxHeap<>();
        for (HeapItem<T> item : elements) {
            clone.elements.add(new HeapItem<>(item.getId(), item.getPriority(), item.getPayload()));
        }
        for (int i = 0; i < clone.elements.size(); i++) {
            clone.idToIndexMap.put(clone.elements.get(i).getId(), i);
        }

        List<HeapItem<T>> topK = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            topK.add(clone.extractMax());
        }

        return topK;
    }

    /**
     * Validates that the max-heap invariant holds for all nodes.
     */
    public synchronized boolean isHeapValid() {
        int size = elements.size();
        for (int i = 0; i < size; i++) {
            int left = leftChild(i);
            int right = rightChild(i);

            if (left < size && elements.get(left).getPriority() > elements.get(i).getPriority()) {
                return false;
            }
            if (right < size && elements.get(right).getPriority() > elements.get(i).getPriority()) {
                return false;
            }
        }
        return true;
    }

    public synchronized void clear() {
        elements.clear();
        idToIndexMap.clear();
        traceHistory.clear();
    }
}
