package com.example.cache;

import java.util.HashMap;
import java.util.Map;

public class CacheNode<K, V> {
    private final String nodeId;
    private final int capacity;
    private final Map<K, V> store = new HashMap<>();
    private final EvictionPolicy<K> evictionPolicy;

    public CacheNode(String nodeId, int capacity, EvictionPolicy<K> evictionPolicy) {
        this.nodeId = nodeId;
        this.capacity = capacity;
        this.evictionPolicy = evictionPolicy;
    }

    public synchronized V get(K key) {
        V value = store.get(key);
        if (value != null) {
            evictionPolicy.onAccess(key);
        }
        return value;
    }

    public synchronized void put(K key, V value) {
        if (store.containsKey(key)) {
            store.put(key, value);
            evictionPolicy.onAccess(key);
            return;
        }
        if (store.size() >= capacity) {
            K evictedKey = evictionPolicy.evict();
            store.remove(evictedKey);
            System.out.println("[" + nodeId + "] evicted key: " + evictedKey);
        }
        store.put(key, value);
        evictionPolicy.onPut(key);
    }

    public String getNodeId() { return nodeId; }
}
