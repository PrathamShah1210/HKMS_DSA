package com.hkms.core.trie;

import java.util.*;

/**
 * Node within the Prefix Trie.
 */
public class TrieNode {

    private final Map<Character, TrieNode> children;
    private boolean isEndOfWord;
    private final Set<Long> paperIds;
    private int frequency;

    public TrieNode() {
        this.children = new HashMap<>();
        this.paperIds = new HashSet<>();
        this.isEndOfWord = false;
        this.frequency = 0;
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public TrieNode getChild(char ch) {
        return children.get(ch);
    }

    public void putChild(char ch, TrieNode node) {
        children.put(ch, node);
    }

    public void removeChild(char ch) {
        children.remove(ch);
    }

    public boolean hasChild(char ch) {
        return children.containsKey(ch);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    public Set<Long> getPaperIds() {
        return Collections.unmodifiableSet(paperIds);
    }

    public void addPaperId(Long id) {
        if (id != null) {
            paperIds.add(id);
        }
    }

    public boolean removePaperId(Long id) {
        return paperIds.remove(id);
    }

    public int getPaperCount() {
        return paperIds.size();
    }

    public int getFrequency() {
        return frequency;
    }

    public void incrementFrequency() {
        this.frequency++;
    }
}
