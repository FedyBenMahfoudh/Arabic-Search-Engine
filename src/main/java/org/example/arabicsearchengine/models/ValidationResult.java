package org.example.arabicsearchengine.models;

public class ValidationResult {

    private final boolean valid;
    private final Root identifiedRoot;
    private final Pattern identifiedPattern;
    private final String explanation;

    private ValidationResult(boolean valid, Root root, Pattern pattern, String explanation) {
        this.valid = valid;
        this.identifiedRoot = root;
        this.identifiedPattern = pattern;
        this.explanation = explanation;
    }

    /**
     * Creates a successful validation result.
     */
    public static ValidationResult success(Root root, Pattern pattern, String explanation) {
        return new ValidationResult(true, root, pattern, explanation);
    }

    /**
     * Creates a failed validation result.
     */
    public static ValidationResult failure(String explanation) {
        return new ValidationResult(false, null, null, explanation);
    }

    public boolean isValid() {
        return valid;
    }

    public Root getIdentifiedRoot() {
        return identifiedRoot;
    }

    public Pattern getIdentifiedPattern() {
        return identifiedPattern;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        if (valid) {
            return "✓ VALID: " + explanation +
                    "\n  Root: " + (identifiedRoot != null ? identifiedRoot.getRootLetters() : "N/A") +
                    "\n  Pattern: " + (identifiedPattern != null ? identifiedPattern.getPatternId() : "N/A");
        } else {
            return "✗ INVALID: " + explanation;
        }
    }
}
