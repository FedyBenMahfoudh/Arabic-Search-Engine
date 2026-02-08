package org.example.arabicsearchengine.repositories;


import org.example.arabicsearchengine.datastructures.hashtable.HashTable;
import org.example.arabicsearchengine.models.Pattern;

import java.util.List;
import java.util.stream.Collectors;

public class PatternRepository {

    private final HashTable<String, Pattern> patternTable;

    public PatternRepository() {
        this.patternTable = new HashTable<>();
    }

    /**Saves a pattern to the hash table.*/
    public void save(Pattern pattern) {
        patternTable.put(pattern.getPatternId(), pattern);
    }

    /** Finds a pattern by its ID.*/
    public Pattern findById(String patternId) {
        return patternTable.get(patternId);
    }

    /**Checks if a pattern exists.*/
    public boolean exists(String patternId) {
        return patternTable.contains(patternId);
    }

    /**Deletes a pattern by its ID.*/
    public Pattern delete(String patternId) {
        return patternTable.remove(patternId);
    }

    /**Returns all patterns.*/
    public List<Pattern> findAll() {
        return patternTable.values();
    }

    /**Returns patterns filtered by category.*/
    public List<Pattern> findByCategory(String category) {
        return patternTable.values().stream()
                .filter(p -> p.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    /**Returns the number of stored patterns.*/
    public int count() {
        return patternTable.getSize();
    }

    /**Checks if repository is empty.*/
    public boolean isEmpty() {
        return patternTable.isEmpty();
    }

    /**Clears all patterns.*/
    public void clear() {
        patternTable.clear();
    }

    /**Prints hash table statistics for debugging.*/
    public void printStats() {
        patternTable.printStats();
    }

}
