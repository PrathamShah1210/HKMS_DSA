package com.hkms.core.avl;

/**
 * Node within a self-balancing AVL Binary Search Tree.
 */
public class AvlNode<K extends Comparable<K>, V> {

    private K key;
    private V value;
    private int height;
    private AvlNode<K, V> left;
    private AvlNode<K, V> right;

    public AvlNode(K key, V value) {
        this.key = key;
        this.value = value;
        this.height = 1;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public AvlNode<K, V> getLeft() {
        return left;
    }

    public void setLeft(AvlNode<K, V> left) {
        this.left = left;
    }

    public AvlNode<K, V> getRight() {
        return right;
    }

    public void setRight(AvlNode<K, V> right) {
        this.right = right;
    }

    public int getBalanceFactor() {
        int leftH = (left != null) ? left.getHeight() : 0;
        int rightH = (right != null) ? right.getHeight() : 0;
        return leftH - rightH;
    }

    public void updateHeight() {
        int leftH = (left != null) ? left.getHeight() : 0;
        int rightH = (right != null) ? right.getHeight() : 0;
        this.height = 1 + Math.max(leftH, rightH);
    }
}
