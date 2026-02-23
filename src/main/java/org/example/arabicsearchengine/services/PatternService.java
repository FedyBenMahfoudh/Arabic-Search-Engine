package org.example.arabicsearchengine.services;

import org.example.arabicsearchengine.models.Pattern;
import org.example.arabicsearchengine.repositories.PatternRepository;
import org.example.arabicsearchengine.utils.FileLoader;

import java.io.IOException;
import java.util.List;

public class PatternService {
    private final PatternRepository patternRepository;

    public PatternService(PatternRepository patternRepository) {
        this.patternRepository = patternRepository;
    }

    public int loadPatternsFromFile(String filePath) throws IOException {
        FileLoader.populatePatternRepository(patternRepository, filePath);
        return patternRepository.count();
    }

    public void addPattern(String patternId, String structure, String description) {
        Pattern pattern = new Pattern(patternId, structure, description);
        patternRepository.save(pattern);
    }

    public void modifyPattern(String patternId, String newStructure, String newDescription) {
        Pattern existing = patternRepository.findById(patternId);
        if (existing != null) {
            Pattern updated = new Pattern(patternId,
                    newStructure != null ? newStructure : existing.getStructure(),
                    newDescription != null ? newDescription : existing.getDescription());
            patternRepository.save(updated);
        }
    }

    /** Retrieves a pattern by ID. */
    public Pattern getPattern(String patternId) {
        return patternRepository.findById(patternId);
    }

    public boolean patternExists(String patternId) {
        return patternRepository.exists(patternId);
    }

    public void deletePattern(String patternId) {
        patternRepository.delete(patternId);
    }

    public List<Pattern> getAllPatterns() {
        return patternRepository.findAll();
    }

    /*
     * public List<Pattern> getPatternsByCategory(String category) {
     * return patternRepository.findByCategory(category);
     * }
     */

    public int getPatternCount() {
        return patternRepository.count();
    }

    /** Initializes default Arabic morphological patterns from file. */
    public void initializeDefaultPatterns() {
        try {
            // Load patterns from classpath resource
            String resourcePath = "/org/example/arabicsearchengine/data/patterns.txt";
            List<Pattern> patterns = FileLoader.loadPatternsFromResource(resourcePath);
            for (Pattern pattern : patterns) {
                patternRepository.save(pattern);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load patterns from file. " + e.getMessage());
            // Fallback to manual initialization if file is not found
            loadDefaultPatternsManually();
        }
    }

    /** Fallback method to load default patterns manually. */
    private void loadDefaultPatternsManually() {
        // فاعل (fa'il) - Active Participle
        addPattern("فاعل", "فاعل", "Active Participle - one who does");

        // مفعول (maf'ul) - Passive Participle
        addPattern("مفعول", "مفعول", "Passive Participle - that which is done");

        // افتعل (ifta'ala) - Form VIII
        addPattern("افتعل", "افتعل", "Form VIII - Reflexive/Reciprocal");

        // تفعيل (taf'il) - Verbal Noun Form II
        addPattern("تفعيل", "تفعيل", "Verbal Noun Form II - Intensive");

        // استفعال (istif'al) - Form X Verbal Noun
        addPattern("استفعال", "استفعال", "Form X Verbal Noun - Seeking/Requesting");

        // فعّال (fa''al) - Intensive Active
        addPattern("فعّال", "فعّال", "Intensive Active - one who does habitually");

        // مفعل (maf'al) - Place/Time noun
        addPattern("مفعل", "مفعل", "Place or Time noun");

        // فعيل (fa'il) - Adjective form
        addPattern("فعيل", "فعيل", "Adjective form");
    }

    /** Gets the underlying repository */
    public PatternRepository getRepository() {
        return patternRepository;
    }
}
