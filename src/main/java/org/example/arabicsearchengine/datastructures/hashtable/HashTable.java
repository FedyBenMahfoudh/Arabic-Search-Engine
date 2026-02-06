package org.example.arabicsearchengine.datastructures.hashtable;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Hash Table implementation for storing Arabic morphological patterns.
 * Uses chaining for collision resolution.
 * 
 * @param <K> Key type (typically String for pattern IDs)
 * @param <V> Value type (typically Pattern objects)
 */
public class HashTable<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    private HashNode<K, V>[] buckets;
    private int size;
    private int capacity;

    public HashTable() {
        this.capacity = DEFAULT_CAPACITY;
        this.buckets = new HashNode[capacity];
        this.size = 0;
    }

    public HashTable(int initialCapacity) {
        this.capacity = initialCapacity;
        this.buckets = new HashNode[capacity];
        this.size = 0;
    }
    //  ---- Getters and setters ----

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    //  ---- Core Operations ----
    public int getIndex(K key) {
        String keyStr = key.toString();
        return HashFunction.hash(keyStr,capacity);
    }

    public void put(K key,V value){
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        if(getLoadFactor() >= LOAD_FACTOR_THRESHOLD) {
            resize();
        }

        int index = getIndex(key);
        HashNode<K, V> head = buckets[index];
        HashNode<K, V> node = head;

        while(node != null){
            if(key.equals(node.getKey())){
                node.setValue(value);
                return;
            }
            node = node.getNext();
        }

        HashNode<K,V> newNode = new HashNode<>(key, value);
        newNode.setNext(head);
        buckets[index] = newNode;
        size++;
    }

    public V get(K key){
        if(key == null){
            return null;
        }

        int index = getIndex(key);
        HashNode<K, V> node = buckets[index];
        while(node != null){
            if(node.getKey().equals(key)){
                return node.getValue();
            }
            node = node.getNext();
        }
        return null;
    }

    public V remove(K key){
        if(key == null){
            return null;
        }

        int index = getIndex(key);
        HashNode<K, V> current = buckets[index];
        HashNode<K, V> prev = null;

        while(current != null){
            if(current.getKey().equals(key)){
                if(prev == null){
                    buckets[index] = current.getNext();
                }else{
                    prev.setNext(current.getNext());
                }
                size--;
                return current.getValue();
            }
            prev = current;
            current = current.getNext();
        }

        return null;
    }

    public boolean contains(K key){
        return get(key) != null;
    }

    private void resize() {
        int newCapacity = capacity * 2;
        HashNode<K, V>[] oldBuckets = buckets;

        buckets = new HashNode[newCapacity];
        capacity = newCapacity;
        size = 0;

        // Rehash all existing entries
        for (HashNode<K, V> head : oldBuckets) {
            HashNode<K, V> current = head;
            while (current != null) {
                put(current.key, current.getValue());
                current = current.getNext();
            }
        }
    }

    private void clear(){
        buckets = new HashNode[capacity];
        size = 0;
    }

    //  -- Utility Functions

    public boolean isEmpty(){
        return size == 0;
    }

    public double getLoadFactor(){
        return (double) getSize() / getCapacity();
    }

    /**
     * Prints table statistics for debugging.
     */
    public void printStats() {
        System.out.println("=== Hash Table Statistics ===");
        System.out.println("Size: " + size);
        System.out.println("Capacity: " + capacity);
        System.out.println("Load Factor: " + String.format("%.2f", getLoadFactor()));

        int maxChain = 0;
        int emptyBuckets = 0;
        for (HashNode<K, V> head : buckets) {
            if (head == null) {
                emptyBuckets++;
            } else {
                int chainLength = 0;
                HashNode<K, V> current = head;
                while (current != null) {
                    chainLength++;
                    current = current.getNext();
                }
                maxChain = Math.max(maxChain, chainLength);
            }
        }
        System.out.println("Empty Buckets: " + emptyBuckets);
        System.out.println("Max Chain Length: " + maxChain);
    }

    /**
     * Returns all keys in the table.
     */
    public List<K> keys() {
        List<K> keyList = new ArrayList<>();
        for (HashNode<K, V> head : buckets) {
            HashNode<K, V> current = head;
            while (current != null) {
                keyList.add(current.key);
                current = current.getNext();
            }
        }
        return keyList;
    }

    /**
     * Returns all values in the table.
     */
    public List<V> values() {
        List<V> valueList = new ArrayList<>();
        for (HashNode<K, V> head : buckets) {
            HashNode<K, V> current = head;
            while (current != null) {
                valueList.add(current.getValue());
                current = current.getNext();
            }
        }
        return valueList;
    }

}
