package org.example.arabicsearchengine.services;

import org.example.arabicsearchengine.models.DerivedWord;
import org.example.arabicsearchengine.models.Pattern;
import org.example.arabicsearchengine.models.Root;


import java.util.List;

public class MorphologyService {

    /**Generates a derived word by applying a pattern to a root.
     * If the derived word already exists in the root's derived word list,
     * its frequency is incremented. Otherwise, it is added as a new entry.*/
    public DerivedWord generateWord(Root root, Pattern pattern) {
        String word = pattern.applyToRoot(root);
        DerivedWord derivedWord = new DerivedWord(word, root, pattern);
        root.addDerivedWord(derivedWord);
        return derivedWord;
    }

    /**Generates all possible words from a root using all provided patterns.
     * Each derived word is checked against the root's existing list via addDerivedWord.*/
    public List<DerivedWord> generateAllWords(Root root, List<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            generateWord(root, pattern);
        }
        return root.getDerivedWords();
    }

    /**Attempts to decompose a word to find its root and pattern.*/
    public DecompositionResult decomposeWord(String word, List<Root> possibleRoots, List<Pattern> patterns) {
        for (Root root : possibleRoots) {
            for (Pattern pattern : patterns) {
                String generated = pattern.applyToRoot(root);
                if (generated.equals(word)) {
                    return new DecompositionResult(root, pattern, true);
                }
            }
        }
        return new DecompositionResult(null, null, false);
    }

    /**Checks if a word can be derived from a specific root using any pattern.*/
    public Pattern findMatchingPattern(String word, Root root, List<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            String generated = pattern.applyToRoot(root);
            if (generated.equals(word)) {
                return pattern;
            }
        }
        return null;
    }

    public static class DecompositionResult {
        private final Root root;
        private final Pattern pattern;
        private final boolean found;

        public DecompositionResult(Root root, Pattern pattern, boolean found) {
            this.root = root;
            this.pattern = pattern;
            this.found = found;
        }

        public Root getRoot() { return root; }
        public Pattern getPattern() { return pattern; }
        public boolean isFound() { return found; }
    }
}
