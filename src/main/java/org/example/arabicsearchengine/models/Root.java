package org.example.arabicsearchengine.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Root implements Comparable<Root> {
    private final String rootLetters;
    private List<DerivedWord>  derivedWords;

    public Root(String rootLetters) {
        if (rootLetters == null || rootLetters.length() != 3) {
            throw new IllegalArgumentException("Root must contain exactly 3 Arabic letters");
        }
        this.rootLetters = rootLetters;
        this.derivedWords = new ArrayList<>();
    }

    public String getRootLetters() {
        return rootLetters;
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

    public List<DerivedWord> getDerivedWords() {
        return derivedWords;
    }

    public void setDerivedWords(List<DerivedWord> derivedWords) {
        this.derivedWords = derivedWords;
    }

    public void addDerivedWord(DerivedWord derivedWord) {
        if(!getDerivedWords().contains(derivedWord)) {
            this.derivedWords.add(derivedWord);
        }else{
            for (DerivedWord derivedWord1 : derivedWords) {
                if (derivedWord1.equals(derivedWord)) {
                    derivedWord1.incrementFrequency();
                    return;
                }
            }
        }
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
                '}';
    }
}
