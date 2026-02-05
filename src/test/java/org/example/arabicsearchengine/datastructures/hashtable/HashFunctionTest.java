package org.example.arabicsearchengine.datastructures.hashtable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashFunctionTest {

    private static final int TABLE_SIZE = 101;

    @Test
    @DisplayName("hash() returns 0 when key is null")
    void testHashWithNullKey() {
        int result = HashFunction.hash(null, TABLE_SIZE);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("hash() returns 0 when key is empty")
    void testHashWithEmptyKey() {
        int result = HashFunction.hash("", TABLE_SIZE);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("hash() handles Arabic Unicode text correctly")
    void testHashWithArabicText() {
        String key = "فاعل";
        int result = HashFunction.hash(key, TABLE_SIZE);

        assertTrue(result >= 0 && result < TABLE_SIZE);
    }

    @Test
    @DisplayName("hash() is deterministic for the same input")
    void testHashConsistency() {
        String key = "فاعل";
        int hash1 = HashFunction.hash(key, TABLE_SIZE);
        int hash2 = HashFunction.hash(key, TABLE_SIZE);

        assertEquals(hash1, hash2, "Hash should be deterministic");
    }

    @Test
    @DisplayName("hash() produces different values for different Arabic keys")
    void testHashDifferentKeys() {
        String key1 = "فاعل";
        String key2 = "مفعول";

        int hash1 = HashFunction.hash(key1, TABLE_SIZE);
        int hash2 = HashFunction.hash(key2, TABLE_SIZE);

        assertNotEquals(hash1, hash2,
                "Different keys should likely produce different hashes");
    }

    @Test
    @DisplayName("hash2() returns valid value when key is null")
    void testHash2WithNullKey() {
        int result = HashFunction.hash2(null, TABLE_SIZE);
        assertTrue(result >= 1 && result < TABLE_SIZE);
    }

    @Test
    @DisplayName("hash2() returns valid value when key is empty")
    void testHash2WithEmptyKey() {
        int result = HashFunction.hash2("", TABLE_SIZE);
        assertTrue(result >= 1 && result < TABLE_SIZE);
    }

    @Test
    @DisplayName("hash2() handles Arabic Unicode text correctly")
    void testHash2WithArabicText() {
        String key = "بحث";
        int result = HashFunction.hash2(key, TABLE_SIZE);

        assertTrue(result >= 1 && result < TABLE_SIZE);
    }

    @Test
    @DisplayName("hash2() is deterministic for the same input")
    void testHash2Consistency() {
        String key = "محرك";
        int hash1 = HashFunction.hash2(key, TABLE_SIZE);
        int hash2 = HashFunction.hash2(key, TABLE_SIZE);

        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("hash2() result is in range [1, tableSize - 1]")
    void testHash2Range() {
        String key = "بيانات";
        int result = HashFunction.hash2(key, TABLE_SIZE);

        assertTrue(result >= 1 && result <= TABLE_SIZE - 1,
                "hash2 must be in range [1, tableSize-1]");
    }
}
