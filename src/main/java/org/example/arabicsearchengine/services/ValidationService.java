package org.example.arabicsearchengine.services;

import org.example.arabicsearchengine.models.DerivedWord;
import org.example.arabicsearchengine.models.Pattern;
import org.example.arabicsearchengine.models.Root;
import org.example.arabicsearchengine.models.ValidationResult;
import org.example.arabicsearchengine.repositories.PatternRepository;
import org.example.arabicsearchengine.repositories.RootRepository;

import java.util.List;

public class ValidationService {
    private final RootRepository rootRepository;
    private final PatternRepository patternRepository;
    private final MorphologyService morphologyService;

    public ValidationService(RootRepository rootRepository,
                             PatternRepository patternRepository,
                             MorphologyService morphologyService) {
        this.rootRepository = rootRepository;
        this.patternRepository = patternRepository;
        this.morphologyService = morphologyService;
    }

    /**
     * Validates a word against a root.
     * Checks if the word can be derived from the given root.
     */
    public ValidationResult validateWord(String word, String rootLetters) {
        // Check if root exists
        Root root = rootRepository.findByLetters(rootLetters);
        if (root == null) {
            return ValidationResult.failure(
                    "Root '" + rootLetters + "' not found in the database");
        }

        // Try to find a matching pattern
        List<Pattern> patterns = patternRepository.findAll();
        Pattern matchingPattern = morphologyService.findMatchingPattern(word, root, patterns);

        if (matchingPattern != null) {
            DerivedWord derived = new DerivedWord(word, root, matchingPattern);
            root.addDerivedWord(derived);
            root.incrementFrequency();
            return ValidationResult.success(root, matchingPattern,
                    "Word '" + word + "' is valid. Derived from root '" + rootLetters +
                            "' using pattern '" + matchingPattern.getPatternId() + "'");
        } else {
            return ValidationResult.failure(
                    "Word '" + word + "' cannot be derived from root '" + rootLetters +
                            "' using any known pattern");
        }
    }
    /**
     * Validates a word against all known roots.
     * Attempts to find any root + pattern combination that produces the word.
     */
    public ValidationResult identifyWord(String word) {
        List<Root> allRoots = rootRepository.findAll();
        List<Pattern> allPatterns = patternRepository.findAll();

        MorphologyService.DecompositionResult result =
                morphologyService.decomposeWord(word, allRoots, allPatterns);

        if (result.isFound()) {
            Root root = result.getRoot();
            Pattern pattern = result.getPattern();
            DerivedWord derived = new DerivedWord(word, root, pattern);
            root.addDerivedWord(derived);
            root.incrementFrequency();
            return ValidationResult.success(result.getRoot(), result.getPattern(),
                    "Word '" + word + "' identified. Root: '" + result.getRoot().getRootLetters() +
                            "', Pattern: '" + result.getPattern().getPatternId() + "'");
        } else {
            return ValidationResult.failure(
                    "Word '" + word + "' could not be matched to any known root+pattern combination");
        }
    }

    /**Validates that a string is a valid triliteral Arabic root.*/
    public boolean isValidRootFormat(String root) {
        if (root == null || root.length() != 3) {
            return false;
        }
        // Check that all characters are Arabic letters
        for (char c : root.toCharArray()) {
            if (c < '\u0621' || c > '\u064A') {  // Arabic letter range
                return false;
            }
        }
        return true;
    }
}
