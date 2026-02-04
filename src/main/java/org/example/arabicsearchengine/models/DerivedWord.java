package org.example.arabicsearchengine.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class DerivedWord {
    private final String word;
    private final Root root;
    private final Pattern pattern;
    private int frequency;
    private boolean validated;

    public DerivedWord(String word, Root root, Pattern pattern) {
        this.word = word;
        this.root = root;
        this.pattern = pattern;
        this.frequency = 0;
        this.validated = true;
    }

    public String getWord() {
        return word;
    }

    public Root getRoot() {
        return root;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public int getFrequency() {
        return frequency;
    }

    public boolean isValidated() {
        return validated;
    }

    public void incrementFrequency() {
        this.frequency++;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DerivedWord that = (DerivedWord) o;
        return Objects.equals(word, that.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }

    @Override
    public String toString() {
        return "DerivedWord{" +
                "word='" + word + '\'' +
                ", root=" + (root != null ? root.getRootLetters() : "null") +
                ", pattern=" + (pattern != null ? pattern.getPatternId() : "null") +
                '}';
    }
}
