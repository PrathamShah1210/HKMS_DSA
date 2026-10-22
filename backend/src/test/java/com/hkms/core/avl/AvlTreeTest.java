package com.hkms.core.avl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AVL Tree Core Balancing & Rotation Tests")
class AvlTreeTest {

    private AvlTree<Long, String> avlTree;

    @BeforeEach
    void setUp() {
        avlTree = new AvlTree<>();
    }

    @Test
    @DisplayName("LL Rotation: Inserting 30, 20, 10 should trigger a Single Right Rotation")
    void testLeftLeftRotation() {
        avlTree.insert(30L, "Paper 30");
        avlTree.insert(20L, "Paper 20");
        avlTree.insert(10L, "Paper 10");

        // After LL rotation, 20 should be root with left 10 and right 30
        assertNotNull(avlTree.getRoot());
        assertEquals(20L, avlTree.getRoot().getKey());
        assertEquals(10L, avlTree.getRoot().getLeft().getKey());
        assertEquals(30L, avlTree.getRoot().getRight().getKey());
        assertEquals(2, avlTree.getHeight());
        assertTrue(avlTree.isAvlTreeValid());

        boolean hasLlRotation = avlTree.getRotationHistory().stream()
                .anyMatch(e -> e.getRotationType() == RotationType.LEFT_LEFT);
        assertTrue(hasLlRotation, "Must record a LEFT_LEFT rotation event");
    }

    @Test
    @DisplayName("RR Rotation: Inserting 10, 20, 30 should trigger a Single Left Rotation")
    void testRightRightRotation() {
        avlTree.insert(10L, "Paper 10");
        avlTree.insert(20L, "Paper 20");
        avlTree.insert(30L, "Paper 30");

        // After RR rotation, 20 should be root with left 10 and right 30
        assertNotNull(avlTree.getRoot());
        assertEquals(20L, avlTree.getRoot().getKey());
        assertEquals(10L, avlTree.getRoot().getLeft().getKey());
        assertEquals(30L, avlTree.getRoot().getRight().getKey());
        assertEquals(2, avlTree.getHeight());
        assertTrue(avlTree.isAvlTreeValid());

        boolean hasRrRotation = avlTree.getRotationHistory().stream()
                .anyMatch(e -> e.getRotationType() == RotationType.RIGHT_RIGHT);
        assertTrue(hasRrRotation, "Must record a RIGHT_RIGHT rotation event");
    }

    @Test
    @DisplayName("LR Rotation: Inserting 30, 10, 20 should trigger a Double Left-Right Rotation")
    void testLeftRightRotation() {
        avlTree.insert(30L, "Paper 30");
        avlTree.insert(10L, "Paper 10");
        avlTree.insert(20L, "Paper 20");

        assertEquals(20L, avlTree.getRoot().getKey());
        assertEquals(10L, avlTree.getRoot().getLeft().getKey());
        assertEquals(30L, avlTree.getRoot().getRight().getKey());
        assertEquals(2, avlTree.getHeight());
        assertTrue(avlTree.isAvlTreeValid());

        boolean hasLrRotation = avlTree.getRotationHistory().stream()
                .anyMatch(e -> e.getRotationType() == RotationType.LEFT_RIGHT);
        assertTrue(hasLrRotation, "Must record a LEFT_RIGHT rotation event");
    }

    @Test
    @DisplayName("RL Rotation: Inserting 10, 30, 20 should trigger a Double Right-Left Rotation")
    void testRightLeftRotation() {
        avlTree.insert(10L, "Paper 10");
        avlTree.insert(30L, "Paper 30");
        avlTree.insert(20L, "Paper 20");

        assertEquals(20L, avlTree.getRoot().getKey());
        assertEquals(10L, avlTree.getRoot().getLeft().getKey());
        assertEquals(30L, avlTree.getRoot().getRight().getKey());
        assertEquals(2, avlTree.getHeight());
        assertTrue(avlTree.isAvlTreeValid());

        boolean hasRlRotation = avlTree.getRotationHistory().stream()
                .anyMatch(e -> e.getRotationType() == RotationType.RIGHT_LEFT);
        assertTrue(hasRlRotation, "Must record a RIGHT_LEFT rotation event");
    }

    @Test
    @DisplayName("Should maintain AVL balancing invariant across 100 sequential insertions")
    void testMassiveSequentialInsertions() {
        for (long i = 1; i <= 100; i++) {
            avlTree.insert(i, "Paper " + i);
            assertTrue(avlTree.isAvlTreeValid(), "AVL invariant broken after inserting " + i);
        }

        assertEquals(100, avlTree.size());
        // For 100 nodes, maximum AVL height <= 1.44 * log2(100 + 2) ≈ 10
        assertTrue(avlTree.getHeight() <= 10, "Height was " + avlTree.getHeight() + ", expected <= 10");

        // Verify in-order traversal is sorted
        List<String> inOrder = avlTree.inOrderTraversal();
        assertEquals(100, inOrder.size());
        assertEquals("Paper 1", inOrder.get(0));
        assertEquals("Paper 100", inOrder.get(99));
    }

    @Test
    @DisplayName("Should search for existing papers in O(log N) and return null for missing")
    void testSearch() {
        avlTree.insert(50L, "Database Systems");
        avlTree.insert(25L, "Algorithms");
        avlTree.insert(75L, "Operating Systems");

        assertEquals("Algorithms", avlTree.search(25L));
        assertEquals("Database Systems", avlTree.search(50L));
        assertEquals("Operating Systems", avlTree.search(75L));
        assertNull(avlTree.search(999L));
        assertTrue(avlTree.contains(50L));
        assertFalse(avlTree.contains(999L));
    }

    @Test
    @DisplayName("Should delete leaf nodes and internal nodes with 2 children while maintaining balance")
    void testDeletionWithRebalance() {
        long[] keys = {40, 20, 60, 10, 30, 50, 70, 25};
        for (long k : keys) {
            avlTree.insert(k, "Paper " + k);
        }

        assertTrue(avlTree.isAvlTreeValid());
        assertEquals(8, avlTree.size());

        // Delete leaf
        assertTrue(avlTree.delete(25L));
        assertEquals(7, avlTree.size());
        assertNull(avlTree.search(25L));
        assertTrue(avlTree.isAvlTreeValid());

        // Delete node with 2 children (root 40)
        assertTrue(avlTree.delete(40L));
        assertEquals(6, avlTree.size());
        assertNull(avlTree.search(40L));
        assertTrue(avlTree.isAvlTreeValid());

        // Verify remaining elements exist
        assertEquals("Paper 20", avlTree.search(20L));
        assertEquals("Paper 60", avlTree.search(60L));
        assertEquals("Paper 50", avlTree.search(50L));
    }
}
