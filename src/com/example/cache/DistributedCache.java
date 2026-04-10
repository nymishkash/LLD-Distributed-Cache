package com.example.cache;

import java.util.ArrayList;
import java.util.List;

public class DistributedCache<K, V> {
    private final List<CacheNode<K, V>> nodes;
    private final DistributionStrategy strategy;
    private final Database<K, V> database;

    public DistributedCache(int nodeCount, int capacityPerNode, DistributionStrategy strategy,
                            EvictionPolicyFactory<K> evictionFactory, Database<K, V> database) {
        this.strategy = strategy;
        this.database = database;
        this.nodes = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            nodes.add(new CacheNode<>("Node-" + i, capacityPerNode, evictionFactory.create()));
        }
    }

    public V get(K key) {
        CacheNode<K, V> node = getNode(key);
        V value = node.get(key);
        if (value != null) {
            System.out.println("[" + node.getNodeId() + "] cache HIT for key: " + key);
            return value;
        }
        System.out.println("[" + node.getNodeId() + "] cache MISS for key: " + key);
        value = database.get(key);
        if (value != null) {
            node.put(key, value);
        }
        return value;
    }

    public void put(K key, V value) {
        CacheNode<K, V> node = getNode(key);
        node.put(key, value);
        System.out.println("[" + node.getNodeId() + "] stored key: " + key);
    }

    private CacheNode<K, V> getNode(K key) {
        int index = strategy.getNodeIndex(key.toString(), nodes.size());
        return nodes.get(index);
    }
}
