package org.example.arabicsearchengine.datastructures.hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    private HashTable<String, String> hashTable;

    @BeforeEach
    void setUp() {
        hashTable = new HashTable<>();
    }

    @Test
    void testPutAndGet() {
        hashTable.put("مفعول", "Pattern1");
        hashTable.put("فاعل", "Pattern2");

        assertEquals("Pattern1", hashTable.get("مفعول"));
        assertEquals("Pattern2", hashTable.get("فاعل"));
        assertNull(hashTable.get("غير موجود"));
    }

    @Test
    void testUpdateValue() {
        hashTable.put("مفعول", "Pattern1");
        hashTable.put("مفعول", "UpdatedPattern");

        assertEquals("UpdatedPattern", hashTable.get("مفعول"));
    }

    @Test
    void testRemove() {
        hashTable.put("مفعول", "Pattern1");
        hashTable.put("فاعل", "Pattern2");

        String removed = hashTable.remove("مفعول");
        assertEquals("Pattern1", removed);
        assertNull(hashTable.get("مفعول"));
        assertEquals(1, hashTable.getSize());

        assertNull(hashTable.remove("غير موجود"));
    }

    @Test
    void testContains() {
        hashTable.put("مفعول", "Pattern1");
        assertTrue(hashTable.contains("مفعول"));
        assertFalse(hashTable.contains("غير موجود"));
    }

    @Test
    void testKeysAndValues() {
        hashTable.put("مفعول", "Pattern1");
        hashTable.put("فاعل", "Pattern2");

        List<String> keys = hashTable.keys();
        List<String> values = hashTable.values();

        assertTrue(keys.contains("مفعول"));
        assertTrue(keys.contains("فاعل"));
        assertTrue(values.contains("Pattern1"));
        assertTrue(values.contains("Pattern2"));
    }

    @Test
    void testIsEmptyAndLoadFactor() {
        assertTrue(hashTable.isEmpty());
        hashTable.put("مفعول", "Pattern1");
        assertFalse(hashTable.isEmpty());
        assertEquals(1.0 / hashTable.getCapacity(), hashTable.getLoadFactor(), 0.01);
    }

    @Test
    void testResize() {
        // Fill table to trigger resize
        for (int i = 0; i < 20; i++) {
            hashTable.put("Key" + i, "Value" + i);
            hashTable.printStats();
        }
        hashTable.printStats();
        assertTrue(hashTable.getCapacity() > 16); // default capacity is 16
    }

    @Test
    void testPrintStats() {
        hashTable.put("مفعول", "Pattern1");
        hashTable.put("فاعل", "Pattern2");
        hashTable.printStats(); // Just to see the output, no assertions needed
    }
}