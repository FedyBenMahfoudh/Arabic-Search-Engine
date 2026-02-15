package org.example.arabicsearchengine.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Root implements Comparable<Root> {
    private final String rootLetters;
    private List<DerivedWord>  derivedWords;
    private int frequency;
    private boolean validated;

    public Root(String rootLetters) {
        if (rootLetters == null || rootLetters.length() != 3) {
            throw new IllegalArgumentException("Root must contain exactly 3 Arabic letters");
        }
        this.rootLetters = rootLetters;
        this.derivedWords = new ArrayList<>();
        this.frequency = 0;
        this.validated = true;
    }

    public String getRootLetters() {
        return rootLetters;
    }

    public int getFrequency() {
        return frequency;
    }

    public char getR1() {
        return this.getRootLetters().charAt(0);
    }

    public char getR2() {
        return this.getRootLetters().charAt(1);
    }

    public char getR3() {
        return this.getRootLetters().charAt(2);
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void incrementFrequency() {
        this.frequency++;
    }
    public List<DerivedWord> getDerivedWords() {
        return derivedWords;
    }

    public void setDerivedWords(List<DerivedWord> derivedWords) {
        this.derivedWords = derivedWords;
    }

    public void addDerivedWord(DerivedWord derivedWord) {
        this.derivedWords.add(derivedWord);
    }


    @Override
    public int compareTo(Root o) {
        return this.getRootLetters().compareTo(o.getRootLetters());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Root root = (Root) o;
        return Objects.equals(getRootLetters(), root.getRootLetters());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRootLetters());
    }

    @Override
    public String toString() {
        return "Root{" +
                "rootLetters='" + rootLetters + '\'' +
                ", frequency=" + frequency +
                ", validated=" + validated +
                '}';
    }
    public void addOrUpdateDerivedWord(String word, Pattern pattern) {
        for (DerivedWord dw : derivedWords) {
            if (dw.getWord().equals(word)) {
                dw.incrementFrequency();
                return;
            }
        }

        // If not found, create new
        DerivedWord newWord = new DerivedWord(word, this, pattern);
        newWord.incrementFrequency();
        derivedWords.add(newWord);
    }
}
