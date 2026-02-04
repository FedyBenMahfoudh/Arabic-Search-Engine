package org.example.arabicsearchengine.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Root implements Comparable<Root> {
    private final String rootLetters;
    private int frequency;
    private boolean validated;

    public Root(String rootLetters) {
        if (rootLetters == null || rootLetters.length() != 3) {
            throw new IllegalArgumentException("Root must contain exactly 3 Arabic letters");
        }
        this.rootLetters = rootLetters;
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
}
