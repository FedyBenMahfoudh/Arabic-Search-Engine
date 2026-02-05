package org.example.arabicsearchengine.datastructures.hashtable;

/**
 * Node for Hash Table using chaining (linked list).
 * @param <K> Key type
 * @param <V> Value type
 */
public class HashNode<K, V> {
    final K key;
    private V value;
    private HashNode<K, V> next;

    public HashNode(K key, V value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public HashNode<K, V> getNext() {
        return next;
    }

    public void setNext(HashNode<K, V> next) {
        this.next = next;
    }

}
