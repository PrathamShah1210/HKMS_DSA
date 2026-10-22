package com.hkms.core.trie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Trie Prefix Autocomplete & Search Tests")
class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        trie = new Trie();
    }

    @Test
    @DisplayName("Should insert terms and map them to research paper IDs")
    void testInsertAndContains() {
        trie.insert("Transformer", 101L);
        trie.insert("Transformers", 102L);
        trie.insert("Attention", 101L);

        assertTrue(trie.contains("Transformer"));
        assertTrue(trie.contains("transformer")); // Case-insensitive
        assertTrue(trie.contains("Transformers"));
        assertTrue(trie.contains("Attention"));
        assertFalse(trie.contains("Att")); // Prefix exists, but not marked as complete word
        assertFalse(trie.contains("BERT"));

        assertEquals(3, trie.getWordCount());
    }

    @Test
    @DisplayName("Should find all words and paper IDs matching a given prefix")
    void testPrefixSearch() {
        trie.insert("Graph Neural Network", 201L);
        trie.insert("Graph Convolution", 202L);
        trie.insert("Graph Attention Network", 203L);
        trie.insert("Gradient Descent", 204L);

        List<AutocompleteMatch> graphMatches = trie.searchPrefix("graph");
        assertEquals(3, graphMatches.size());

        List<AutocompleteMatch> grMatches = trie.searchPrefix("gr");
        assertEquals(4, grMatches.size());

        List<AutocompleteMatch> nonExistent = trie.searchPrefix("quantum");
        assertTrue(nonExistent.isEmpty());
    }

    @Test
    @DisplayName("Should rank autocomplete suggestions by frequency descending and respect limit")
    void testAutocompleteRanking() {
        // Insert "machine" once
        trie.insert("machine", 1L);

        // Insert "machine learning" 3 times
        trie.insert("machine learning", 2L);
        trie.insert("machine learning", 3L);
        trie.insert("machine learning", 4L);

        // Insert "machine vision" 2 times
        trie.insert("machine vision", 5L);
        trie.insert("machine vision", 6L);

        List<AutocompleteMatch> suggestions = trie.autocomplete("mach", 2);
        assertEquals(2, suggestions.size());

        // Top match must be "machine learning" (frequency 3)
        assertEquals("machine learning", suggestions.get(0).getTerm());
        assertEquals(3, suggestions.get(0).getFrequency());

        // Second match must be "machine vision" (frequency 2)
        assertEquals("machine vision", suggestions.get(1).getTerm());
        assertEquals(2, suggestions.get(1).getFrequency());
    }

    @Test
    @DisplayName("Should cleanly prune branches when a word is deleted while preserving shared prefixes")
    void testDeleteAndPruning() {
        trie.insert("trie", 1L);
        trie.insert("tree", 2L);
        trie.insert("trees", 3L);

        assertEquals(3, trie.getWordCount());

        // Delete "trees" for paper 3
        assertTrue(trie.delete("trees", 3L));
        assertFalse(trie.contains("trees"));
        assertTrue(trie.contains("tree")); // "tree" must remain intact
        assertTrue(trie.contains("trie")); // "trie" must remain intact
        assertEquals(2, trie.getWordCount());

        // If multiple papers share a term, removing one paper should not delete the term
        trie.insert("quantum", 10L);
        trie.insert("quantum", 20L);
        trie.delete("quantum", 10L);
        assertTrue(trie.contains("quantum"), "Term should still exist while other paper ID references it");

        // Now delete for the second paper
        trie.delete("quantum", 20L);
        assertFalse(trie.contains("quantum"));
    }
}
