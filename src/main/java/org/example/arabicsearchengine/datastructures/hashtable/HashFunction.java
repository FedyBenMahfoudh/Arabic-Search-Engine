package org.example.arabicsearchengine.datastructures.hashtable;

/**
 * Custom hash function optimized for Arabic text.
 * Handles Unicode characters in the Arabic Unicode block (U+0600 to U+06FF).
 */
public class HashFunction {

    private static final int PRIME = 31;

    public static int hash(String key,int tableSize){
        if( key == null || key.isEmpty()){
            return 0;
        }
        long hash = 0;
        for(int i = 0; i < key.length(); i++){
            char c = key.charAt(i);
            hash = (hash * PRIME + c) % tableSize;
        }

        // Ensure non-negative result
        return (int) ((hash % tableSize + tableSize) % tableSize);
    }

    public static int hash2(String key, int tableSize) {
        if (key == null || key.isEmpty()) {
            return 1;
        }

        long hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * 17 + key.charAt(i)) % tableSize;
        }

        // Must return value between 1 and tableSize-1 for double hashing
        int result = (int) ((hash % (tableSize - 1) + tableSize - 1) % (tableSize - 1)) + 1;
        return result;
    }

}
