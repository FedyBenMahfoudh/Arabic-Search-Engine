package org.example.arabicsearchengine.services;

import org.example.arabicsearchengine.models.DerivedWord;
import org.example.arabicsearchengine.models.Pattern;
import org.example.arabicsearchengine.models.Root;

import java.util.ArrayList;
import java.util.List;

public class MorphologyService {

    /**Generates a derived word by applying a pattern to a root.*/
    public DerivedWord generateWord(Root root, Pattern pattern) {
        String word = pattern.applyToRoot(root);
        return new DerivedWord(word, root, pattern);
    }

    /**Generates all possible words from a root using all provided patterns.*/
    public List<DerivedWord> generateAllWords(Root root, List<Pattern> patterns) {
        List<DerivedWord> words = new ArrayList<>();
        for (Pattern pattern : patterns) {
            words.add(generateWord(root, pattern));
        }
        return words;
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
