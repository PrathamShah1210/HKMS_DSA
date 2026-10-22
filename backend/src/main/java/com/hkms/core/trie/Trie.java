package com.hkms.core.trie;

import java.util.*;

/**
 * Custom Pure Java Prefix Trie Implementation.
 * Used for instant research paper title & keyword autocomplete.
 *
 * Algorithm Features:
 * - O(L) insertion and search where L is the term length
 * - Dynamic branch pruning on deletion to preserve memory
 * - Prefix exploration via DFS for exhaustive auto-suggestions
 * - Frequency-based result ordering
 */
public class Trie {

    private final TrieNode root;
    private int wordCount;

    public Trie() {
        this.root = new TrieNode();
        this.wordCount = 0;
    }

    public synchronized TrieNode getRoot() {
        return root;
    }

    public synchronized int getWordCount() {
        return wordCount;
    }

    private String normalize(String str) {
        if (str == null) return "";
        return str.trim().toLowerCase();
    }

    /**
     * Inserts a term and associates it with a research paper ID.
     */
    public synchronized void insert(String term, Long paperId) {
        String clean = normalize(term);
        if (clean.isEmpty()) return;

        TrieNode current = root;
        for (int i = 0; i < clean.length(); i++) {
            char ch = clean.charAt(i);
            if (!current.hasChild(ch)) {
                current.putChild(ch, new TrieNode());
            }
            current = current.getChild(ch);
        }

        if (!current.isEndOfWord()) {
            current.setEndOfWord(true);
            wordCount++;
        }
        current.incrementFrequency();
        if (paperId != null) {
            current.addPaperId(paperId);
        }
    }

    /**
     * Checks if exact word exists in the Trie.
     */
    public synchronized boolean contains(String term) {
        String clean = normalize(term);
        if (clean.isEmpty()) return false;

        TrieNode current = root;
        for (int i = 0; i < clean.length(); i++) {
            char ch = clean.charAt(i);
            if (!current.hasChild(ch)) {
                return false;
            }
            current = current.getChild(ch);
        }
        return current != null && current.isEndOfWord();
    }

    /**
     * Gathers all matching terms starting with the given prefix.
     */
    public synchronized List<AutocompleteMatch> searchPrefix(String prefix) {
        String clean = normalize(prefix);
        if (clean.isEmpty()) return Collections.emptyList();

        TrieNode current = root;
        for (int i = 0; i < clean.length(); i++) {
            char ch = clean.charAt(i);
            if (!current.hasChild(ch)) {
                return Collections.emptyList();
            }
            current = current.getChild(ch);
        }

        List<AutocompleteMatch> results = new ArrayList<>();
        collectAllDescendants(current, new StringBuilder(clean), results);
        return results;
    }

    private void collectAllDescendants(TrieNode node, StringBuilder currentPath, List<AutocompleteMatch> results) {
        if (node == null) return;

        if (node.isEndOfWord()) {
            results.add(new AutocompleteMatch(
                    currentPath.toString(),
                    node.getPaperIds(),
                    node.getFrequency()
            ));
        }

        // Traverse children in alphabetical order
        List<Character> sortedChars = new ArrayList<>(node.getChildren().keySet());
        Collections.sort(sortedChars);

        for (char ch : sortedChars) {
            currentPath.append(ch);
            collectAllDescendants(node.getChild(ch), currentPath, results);
            currentPath.deleteCharAt(currentPath.length() - 1); // Backtrack
        }
    }

    /**
     * Top-K autocomplete suggestions sorted by frequency descending.
     */
    public synchronized List<AutocompleteMatch> autocomplete(String prefix, int limit) {
        List<AutocompleteMatch> allMatches = searchPrefix(prefix);
        allMatches.sort((a, b) -> Integer.compare(b.getFrequency(), a.getFrequency()));
        if (allMatches.size() > limit) {
            return new ArrayList<>(allMatches.subList(0, limit));
        }
        return allMatches;
    }

    /**
     * Deletes a word association for a specific paper.
     * Prunes empty nodes recursively if no other words or papers use them.
     * Returns true if the word was found and deleted/unreferenced.
     */
    public synchronized boolean delete(String term, Long paperId) {
        String clean = normalize(term);
        if (clean.isEmpty() || !contains(clean)) return false;

        boolean[] deleted = new boolean[]{false};
        deleteHelper(root, clean, 0, paperId, deleted);
        return deleted[0];
    }

    private boolean deleteHelper(TrieNode current, String word, int index, Long paperId, boolean[] deleted) {
        if (current == null) return false;

        if (index == word.length()) {
            if (!current.isEndOfWord()) {
                return false;
            }

            deleted[0] = true;
            if (paperId != null) {
                current.removePaperId(paperId);
            }

            // Only unmark end-of-word if no more papers are associated or if paperId wasn't specified
            if (current.getPaperIds().isEmpty()) {
                current.setEndOfWord(false);
                wordCount--;
                return !current.hasChildren(); // true means parent can prune this child
            }
            return false;
        }

        char ch = word.charAt(index);
        TrieNode child = current.getChild(ch);
        if (child == null) {
            return false;
        }

        boolean shouldPruneChild = deleteHelper(child, word, index + 1, paperId, deleted);

        if (shouldPruneChild) {
            current.removeChild(ch);
            return !current.isEndOfWord() && !current.hasChildren();
        }

        return false;
    }

    public synchronized void clear() {
        root.getChildren().clear();
        wordCount = 0;
    }
}
