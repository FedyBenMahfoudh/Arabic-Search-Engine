package org.example.arabicsearchengine.services;


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
     * Validates a word against a specific root.
     * Returns OUI/NON with identified pattern if valid.
     */
    public ValidationResult validateWord(String word, String rootLetters) {
        if (!isValidRootFormat(rootLetters)) {
            return ValidationResult.failure("الجذر يجب أن يكون 3 أحرف عربية");
        }

        Root root = rootRepository.findByLetters(rootLetters);
        if (root == null) {
            return ValidationResult.failure("الجذر '" + rootLetters + "' غير موجود في قاعدة البيانات");
        }

        List<Pattern> patterns = patternRepository.findAll();
        if (patterns == null || patterns.isEmpty()) {
            return ValidationResult.failure("لا توجد صيغ متاحة");
        }

        Pattern matchingPattern = morphologyService.findMatchingPattern(word, root, patterns);

        if (matchingPattern != null) {
            // Generate the word through morphologyService which handles:
            //   - if word already exists in root's derivedWords -> increment frequency
            //   - if not -> add it to the list
            morphologyService.generateWord(root, matchingPattern);
            return ValidationResult.success(root, matchingPattern,
                    "✓ نعم - الكلمة '" + word + "' صحيحة - مشتقة من الجذر '" + rootLetters +
                            "' بالصيغة '" + matchingPattern.getPatternId() + "'");
        } else {
            return ValidationResult.failure(
                    "✗ لا - الكلمة '" + word + "' لا يمكن اشتقاقها من الجذر '" + rootLetters + "'");
        }
    }

    /**
     * Identifies a word by finding its root and pattern.
     * Returns OUI/NON with identified root and pattern.
     */
    public ValidationResult identifyWord(String word) {
        List<Root> allRoots = rootRepository.findAll();
        List<Pattern> allPatterns = patternRepository.findAll();

        if (allRoots == null || allRoots.isEmpty() || allPatterns == null || allPatterns.isEmpty()) {
            return ValidationResult.failure("قاعدة البيانات غير كاملة - جذور أو صيغ غير متاحة");
        }

        MorphologyService.DecompositionResult result = morphologyService.decomposeWord(word, allRoots, allPatterns);

        if (result.isFound()) {
            Root root = result.getRoot();
            Pattern pattern = result.getPattern();
            // Generate the word through morphologyService which handles:
            //   - if word already exists in root's derivedWords -> increment frequency
            //   - if not -> add it to the list
            morphologyService.generateWord(root, pattern);
            return ValidationResult.success(root, pattern,
                    "✓ نعم - تم تحديد الكلمة '" + word + "' - الجذر: '" + root.getRootLetters() +
                            "' - الصيغة: '" + pattern.getPatternId() + "'");
        } else {
            return ValidationResult.failure(
                    "✗ لا - لم يتمكن من تحديد الكلمة '" + word + "' (الجذر والصيغة غير متطابقة)");
        }
    }

    /** Validates that a string is a valid 3-letter Arabic root. */
    public boolean isValidRootFormat(String root) {
        if (root == null || root.trim().isEmpty()) {
            return false;
        }
        root = root.trim();
        if (root.length() != 3) {
            return false;
        }
        // Check that all characters are Arabic letters (Unicode range U+0600 to U+06FF)
        for (char c : root.toCharArray()) {
            if (c < '\u0600' || c > '\u06FF') {
                return false;
            }
        }
        return true;
    }
}
