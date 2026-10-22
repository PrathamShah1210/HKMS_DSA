package com.hkms.core.heap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Max Heap Priority Ranking Algorithm Tests")
class MaxHeapTest {

    private MaxHeap<String> heap;

    @BeforeEach
    void setUp() {
        heap = new MaxHeap<>();
    }

    @Test
    @DisplayName("Should maintain Max Heap invariant during insertions")
    void testInsertMaintainsHeap() {
        heap.insert(1L, 50.0, "Paper 1");
        heap.insert(2L, 95.0, "Paper 2");
        heap.insert(3L, 70.0, "Paper 3");
        heap.insert(4L, 88.0, "Paper 4");
        heap.insert(5L, 60.0, "Paper 5");

        assertEquals(5, heap.size());
        assertTrue(heap.isHeapValid());
        assertEquals(95.0, heap.peekMax().getPriority());
        assertEquals(2L, heap.peekMax().getId());
    }

    @Test
    @DisplayName("Should extract maximum elements in strictly descending priority order")
    void testExtractMax() {
        double[] scores = {45.0, 92.5, 33.1, 88.0, 99.9, 12.0, 77.4};
        for (int i = 0; i < scores.length; i++) {
            heap.insert((long) (i + 1), scores[i], "Paper " + (i + 1));
        }

        assertEquals(scores.length, heap.size());
        assertTrue(heap.isHeapValid());

        double previous = Double.MAX_VALUE;
        while (!heap.isEmpty()) {
            HeapItem<String> max = heap.extractMax();
            assertTrue(max.getPriority() <= previous, "Extracted priority must be <= previous");
            previous = max.getPriority();
            assertTrue(heap.isHeapValid(), "Heap invariant must hold after each extraction");
        }

        assertThrows(NoSuchElementException.class, () -> heap.extractMax());
    }

    @Test
    @DisplayName("Should update priority in O(log N) via index map and re-heapify")
    void testUpdatePriority() {
        heap.insert(101L, 40.0, "Paper Low");
        heap.insert(102L, 80.0, "Paper High");
        heap.insert(103L, 60.0, "Paper Medium");

        assertEquals(102L, heap.peekMax().getId());

        // Increase Paper Low's priority to 99.0 (exceeding Paper High)
        assertTrue(heap.updatePriority(101L, 99.0));
        assertTrue(heap.isHeapValid());
        assertEquals(101L, heap.peekMax().getId(), "Paper 101 should now be at the root");

        // Decrease Paper 101's priority to 10.0
        assertTrue(heap.updatePriority(101L, 10.0));
        assertTrue(heap.isHeapValid());
        assertEquals(102L, heap.peekMax().getId(), "Paper 102 should be back at the root");

        // Non-existent ID update should return false
        assertFalse(heap.updatePriority(999L, 50.0));
    }

    @Test
    @DisplayName("Should retrieve top-K elements without altering the original heap")
    void testGetTopKNonDestructive() {
        heap.insert(1L, 10.0, "P1");
        heap.insert(2L, 50.0, "P2");
        heap.insert(3L, 30.0, "P3");
        heap.insert(4L, 90.0, "P4");
        heap.insert(5L, 70.0, "P5");

        List<HeapItem<String>> top3 = heap.getTopK(3);
        assertEquals(3, top3.size());
        assertEquals(90.0, top3.get(0).getPriority());
        assertEquals(70.0, top3.get(1).getPriority());
        assertEquals(50.0, top3.get(2).getPriority());

        // Original heap must retain all 5 elements
        assertEquals(5, heap.size());
        assertEquals(90.0, heap.peekMax().getPriority());
        assertTrue(heap.isHeapValid());
    }

    @Test
    @DisplayName("Should delete arbitrary element by ID and restore heap property")
    void testDeleteArbitrary() {
        heap.insert(1L, 100.0, "Root");
        heap.insert(2L, 80.0, "Child 1");
        heap.insert(3L, 70.0, "Child 2");
        heap.insert(4L, 60.0, "Grandchild 1");

        // Delete an interior node
        assertTrue(heap.delete(2L));
        assertEquals(3, heap.size());
        assertTrue(heap.isHeapValid());

        // Delete root
        assertTrue(heap.delete(1L));
        assertEquals(2, heap.size());
        assertTrue(heap.isHeapValid());
        assertEquals(70.0, heap.peekMax().getPriority());
    }

    @Test
    @DisplayName("Should generate step trace records for visual playback")
    void testTraceGeneration() {
        heap.insert(1L, 10.0, "P1");
        heap.insert(2L, 50.0, "P2"); // Triggers sift-up swap

        List<HeapStepTrace> traces = heap.getTraceHistory();
        assertFalse(traces.isEmpty());

        boolean hasSwapTrace = traces.stream().anyMatch(t -> "SIFT_UP".equals(t.getOperation()));
        assertTrue(hasSwapTrace, "Should capture SIFT_UP swap trace");
    }
}
