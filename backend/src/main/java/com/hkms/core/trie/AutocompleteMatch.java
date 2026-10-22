package com.hkms.core.trie;

import java.util.Set;

/**
 * Output model for prefix search and autocomplete queries.
 */
public class AutocompleteMatch {

    private final String term;
    private final Set<Long> paperIds;
    private final int frequency;

    public AutocompleteMatch(String term, Set<Long> paperIds, int frequency) {
        this.term = term;
        this.paperIds = paperIds;
        this.frequency = frequency;
    }

    public String getTerm() {
        return term;
    }

    public Set<Long> getPaperIds() {
        return paperIds;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return term + " (Freq: " + frequency + ", Papers: " + paperIds.size() + ")";
    }
}
