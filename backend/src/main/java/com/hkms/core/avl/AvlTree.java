package com.hkms.core.avl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Custom Pure Java AVL Tree Implementation.
 * Provides self-balancing binary search tree operations in O(log N) time.
 *
 * Implements:
 * - Single Right Rotation (LL Case)
 * - Single Left Rotation (RR Case)
 * - Double Left-Right Rotation (LR Case)
 * - Double Right-Left Rotation (RL Case)
 * - Node deletion with in-order successor and ancestor rebalancing
 * - Rotation audit history for academic visualization
 */
public class AvlTree<K extends Comparable<K>, V> {

    private AvlNode<K, V> root;
    private int size;
    private final List<RotationEvent> rotationHistory;

    public AvlTree() {
        this.root = null;
        this.size = 0;
        this.rotationHistory = new ArrayList<>();
    }

    public synchronized AvlNode<K, V> getRoot() {
        return root;
    }

    public synchronized int size() {
        return size;
    }

    public synchronized boolean isEmpty() {
        return root == null;
    }

    public synchronized int getHeight() {
        return getNodeHeight(root);
    }

    private int getNodeHeight(AvlNode<K, V> node) {
        return (node == null) ? 0 : node.getHeight();
    }

    public synchronized List<RotationEvent> getRotationHistory() {
        return Collections.unmodifiableList(rotationHistory);
    }

    public synchronized void clearRotationHistory() {
        rotationHistory.clear();
    }

    /**
     * Searches for a value associated with key in O(log N).
     */
    public synchronized V search(K key) {
        if (key == null) return null;
        AvlNode<K, V> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.getKey());
            if (cmp == 0) {
                return current.getValue();
            } else if (cmp < 0) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }
        return null;
    }

    public synchronized boolean contains(K key) {
        return search(key) != null;
    }

    /**
     * Inserts key-value pair and rebalances tree along ancestor path.
     */
    public synchronized void insert(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        root = insertRecursive(root, key, value);
    }

    private AvlNode<K, V> insertRecursive(AvlNode<K, V> node, K key, V value) {
        // 1. Standard BST insertion
        if (node == null) {
            size++;
            return new AvlNode<>(key, value);
        }

        int cmp = key.compareTo(node.getKey());
        if (cmp < 0) {
            node.setLeft(insertRecursive(node.getLeft(), key, value));
        } else if (cmp > 0) {
            node.setRight(insertRecursive(node.getRight(), key, value));
        } else {
            // Update existing key
            node.setValue(value);
            return node;
        }

        // 2. Update height of current ancestor node
        node.updateHeight();

        // 3. Calculate Balance Factor
        int balance = node.getBalanceFactor();

        // 4. Handle 4 rotation cases if unbalanced

        // Left-Left (LL) Case -> Single Right Rotation
        if (balance > 1 && key.compareTo(node.getLeft().getKey()) < 0) {
            rotationHistory.add(new RotationEvent(
                    RotationType.LEFT_LEFT,
                    node.getKey(),
                    balance,
                    "Left child became heavier after left insertion of key " + key
            ));
            return rotateRight(node);
        }

        // Right-Right (RR) Case -> Single Left Rotation
        if (balance < -1 && key.compareTo(node.getRight().getKey()) > 0) {
            rotationHistory.add(new RotationEvent(
                    RotationType.RIGHT_RIGHT,
                    node.getKey(),
                    balance,
                    "Right child became heavier after right insertion of key " + key
            ));
            return rotateLeft(node);
        }

        // Left-Right (LR) Case -> Double Rotation (Left on left child, then Right on node)
        if (balance > 1 && key.compareTo(node.getLeft().getKey()) > 0) {
            rotationHistory.add(new RotationEvent(
                    RotationType.LEFT_RIGHT,
                    node.getKey(),
                    balance,
                    "Left child right-heavy after insertion of key " + key
            ));
            node.setLeft(rotateLeft(node.getLeft()));
            return rotateRight(node);
        }

        // Right-Left (RL) Case -> Double Rotation (Right on right child, then Left on node)
        if (balance < -1 && key.compareTo(node.getRight().getKey()) < 0) {
            rotationHistory.add(new RotationEvent(
                    RotationType.RIGHT_LEFT,
                    node.getKey(),
                    balance,
                    "Right child left-heavy after insertion of key " + key
            ));
            node.setRight(rotateRight(node.getRight()));
            return rotateLeft(node);
        }

        return node;
    }

    /**
     * Deletes a key from the AVL tree and rebalances.
     */
    public synchronized boolean delete(K key) {
        if (key == null || !contains(key)) {
            return false;
        }
        root = deleteRecursive(root, key);
        size--;
        return true;
    }

    private AvlNode<K, V> deleteRecursive(AvlNode<K, V> node, K key) {
        if (node == null) return null;

        int cmp = key.compareTo(node.getKey());
        if (cmp < 0) {
            node.setLeft(deleteRecursive(node.getLeft(), key));
        } else if (cmp > 0) {
            node.setRight(deleteRecursive(node.getRight(), key));
        } else {
            // Found node to delete

            // Case 1 & 2: Node with 0 or 1 child
            if (node.getLeft() == null || node.getRight() == null) {
                AvlNode<K, V> temp = (node.getLeft() != null) ? node.getLeft() : node.getRight();
                if (temp == null) {
                    // No child
                    node = null;
                } else {
                    // One child
                    node = temp;
                }
            } else {
                // Case 3: Node with 2 children
                // Find in-order successor (smallest in the right subtree)
                AvlNode<K, V> successor = getMinValueNode(node.getRight());
                node.setKey(successor.getKey());
                node.setValue(successor.getValue());
                // Delete in-order successor
                node.setRight(deleteRecursive(node.getRight(), successor.getKey()));
            }
        }

        if (node == null) {
            return null;
        }

        // Update height
        node.updateHeight();

        // Balance factor check and rebalance
        int balance = node.getBalanceFactor();

        // Left-Left (LL)
        if (balance > 1 && node.getLeft().getBalanceFactor() >= 0) {
            rotationHistory.add(new RotationEvent(
                    RotationType.LEFT_LEFT, node.getKey(), balance, "Rebalance after deletion (LL)"
            ));
            return rotateRight(node);
        }

        // Left-Right (LR)
        if (balance > 1 && node.getLeft().getBalanceFactor() < 0) {
            rotationHistory.add(new RotationEvent(
                    RotationType.LEFT_RIGHT, node.getKey(), balance, "Rebalance after deletion (LR)"
            ));
            node.setLeft(rotateLeft(node.getLeft()));
            return rotateRight(node);
        }

        // Right-Right (RR)
        if (balance < -1 && node.getRight().getBalanceFactor() <= 0) {
            rotationHistory.add(new RotationEvent(
                    RotationType.RIGHT_RIGHT, node.getKey(), balance, "Rebalance after deletion (RR)"
            ));
            return rotateLeft(node);
        }

        // Right-Left (RL)
        if (balance < -1 && node.getRight().getBalanceFactor() > 0) {
            rotationHistory.add(new RotationEvent(
                    RotationType.RIGHT_LEFT, node.getKey(), balance, "Rebalance after deletion (RL)"
            ));
            node.setRight(rotateRight(node.getRight()));
            return rotateLeft(node);
        }

        return node;
    }

    private AvlNode<K, V> getMinValueNode(AvlNode<K, V> node) {
        AvlNode<K, V> current = node;
        while (current.getLeft() != null) {
            current = current.getLeft();
        }
        return current;
    }

    /**
     * Single Right Rotation (LL Case).
     *
     *       y                              x
     *      / \                            / \
     *     x   T3   == Rotate Right ==>   T1  y
     *    / \                                / \
     *   T1  T2                             T2  T3
     */
    private AvlNode<K, V> rotateRight(AvlNode<K, V> y) {
        AvlNode<K, V> x = y.getLeft();
        AvlNode<K, V> T2 = x.getRight();

        // Perform rotation
        x.setRight(y);
        y.setLeft(T2);

        // Update heights
        y.updateHeight();
        x.updateHeight();

        return x;
    }

    /**
     * Single Left Rotation (RR Case).
     *
     *     x                                y
     *    / \                              / \
     *   T1  y      == Rotate Left ==>    x   T3
     *      / \                          / \
     *     T2  T3                       T1  T2
     */
    private AvlNode<K, V> rotateLeft(AvlNode<K, V> x) {
        AvlNode<K, V> y = x.getRight();
        AvlNode<K, V> T2 = y.getLeft();

        // Perform rotation
        y.setLeft(x);
        x.setRight(T2);

        // Update heights
        x.updateHeight();
        y.updateHeight();

        return y;
    }

    /**
     * In-Order Traversal yielding sorted key-value pairs.
     */
    public synchronized List<V> inOrderTraversal() {
        List<V> result = new ArrayList<>();
        inOrderRecursive(root, result);
        return result;
    }

    private void inOrderRecursive(AvlNode<K, V> node, List<V> result) {
        if (node == null) return;
        inOrderRecursive(node.getLeft(), result);
        result.add(node.getValue());
        inOrderRecursive(node.getRight(), result);
    }

    /**
     * Verifies that the tree satisfies both BST ordering and the AVL balance property (|BF| <= 1).
     */
    public synchronized boolean isAvlTreeValid() {
        return checkAvlInvariants(root) != -1;
    }

    private int checkAvlInvariants(AvlNode<K, V> node) {
        if (node == null) return 0;

        int leftH = checkAvlInvariants(node.getLeft());
        if (leftH == -1) return -1;

        int rightH = checkAvlInvariants(node.getRight());
        if (rightH == -1) return -1;

        // Check BST ordering
        if (node.getLeft() != null && node.getLeft().getKey().compareTo(node.getKey()) >= 0) {
            return -1;
        }
        if (node.getRight() != null && node.getRight().getKey().compareTo(node.getKey()) <= 0) {
            return -1;
        }

        // Check Balance Factor
        int balance = leftH - rightH;
        if (Math.abs(balance) > 1) {
            return -1;
        }

        return 1 + Math.max(leftH, rightH);
    }

    public synchronized void clear() {
        root = null;
        size = 0;
        rotationHistory.clear();
    }
}
