package org.example.arabicsearchengine.utils;

import org.example.arabicsearchengine.models.Pattern;
import org.example.arabicsearchengine.models.Root;
import org.example.arabicsearchengine.repositories.PatternRepository;
import org.example.arabicsearchengine.repositories.RootRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {

    public static List<Root> loadRoots(String filePath) throws IOException {
        List<Root> roots = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#") && line.length() == 3) {
                    try {
                        roots.add(new Root(line));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Skipping invalid root: " + line);
                    }
                }
            }
        }

        return roots;
    }

    /**Loads Arabic roots from classpath resource.*/
    public static List<Root> loadRootsFromResource(String resourcePath) throws IOException {
        List<Root> roots = new ArrayList<>();

        try (InputStream is = FileLoader.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#") && line.length() == 3) {
                    try {
                        roots.add(new Root(line));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Skipping invalid root: " + line);
                    }
                }
            }
        }

        return roots;
    }

    /**Loads morphological patterns from a file.*/
    public static List<Pattern> loadPatterns(String filePath) throws IOException {
        List<Pattern> patterns = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 2) {
                        String id = parts[0].trim();
                        String structure = parts[1].trim();
                        String description = parts.length > 2 ? parts[2].trim() : "";
                        String category = parts.length > 3 ? parts[3].trim() : "general";
                        patterns.add(new Pattern(id, structure, description, category));
                    }
                }
            }
        }

        return patterns;
    }

    /**Loads morphological patterns from classpath resource.*/
    public static List<Pattern> loadPatternsFromResource(String resourcePath) throws IOException {
        List<Pattern> patterns = new ArrayList<>();

        try (InputStream is = FileLoader.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 2) {
                        String id = parts[0].trim();
                        String structure = parts[1].trim();
                        String description = parts.length > 2 ? parts[2].trim() : "";
                        String category = parts.length > 3 ? parts[3].trim() : "general";
                        patterns.add(new Pattern(id, structure, description, category));
                    }
                }
            }
        }

        return patterns;
    }

    /**Populates a RootRepository from a file.*/
    public static void populateRootRepository(RootRepository repo, String filePath) throws IOException {
        List<Root> roots = loadRoots(filePath);
        for (Root root : roots) {
            repo.save(root);
        }
    }

    /**Populates a PatternRepository from a file.*/
    public static void populatePatternRepository(PatternRepository repo, String filePath) throws IOException {
        List<Pattern> patterns = loadPatterns(filePath);
        for (Pattern pattern : patterns) {
            repo.save(pattern);
        }
    }
}
