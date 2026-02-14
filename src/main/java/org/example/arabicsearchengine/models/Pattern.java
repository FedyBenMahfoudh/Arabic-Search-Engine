package org.example.arabicsearchengine.models;

import java.util.Objects;

public class Pattern {
    private final String patternId;      // e.g., "فاعل", "مفعول"
    private final String structure;       // Abstract structure for transformation
    private String description;           // Human-readable description

    // Arabic letters used as placeholders in patterns
    public static final char FA = 'ف';   // Placeholder for R1
    public static final char AIN = 'ع';  // Placeholder for R2
    public static final char LAM = 'ل';  // Placeholder for R3

    public Pattern(String patternId, String structure) {
        this.patternId = patternId;
        this.structure = structure;
        this.description = "";
    }

    public Pattern(String patternId, String structure, String description) {
        this.patternId = patternId;
        this.structure = structure;
        this.description = description;
    }

    /**
     * Applies this pattern to a given root to generate a derived word.
     * Replaces ف with R1, ع with R2, ل with R3.
     *
     * @param root The triliteral root to apply the pattern to
     * @return The generated Arabic word
     */
    public String applyToRoot(Root root) {
        StringBuilder result = new StringBuilder();

        for (char c : structure.toCharArray()) {
            if (c == FA) {
                result.append(root.getR1());
            } else if (c == AIN) {
                result.append(root.getR2());
            } else if (c == LAM) {
                result.append(root.getR3());
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    // --- Getters ---
    public String getPatternId() {
        return patternId;
    }

    public String getStructure() {
        return structure;
    }

    public String getDescription() {
        return description;
    }

    // --- Setters ---
    public void setDescription(String description) {
        this.description = description;
    }

    // --- Object overrides ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pattern pattern = (Pattern) o;
        return Objects.equals(patternId, pattern.patternId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patternId);
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "id='" + patternId + '\'' +
                ", structure='" + structure + '\'' +
                '}';
    }
}
