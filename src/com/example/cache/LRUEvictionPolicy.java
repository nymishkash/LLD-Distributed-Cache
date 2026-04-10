package com.example.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUEvictionPolicy<K> implements EvictionPolicy<K> {
    private final LinkedHashMap<K, Boolean> accessOrder = new LinkedHashMap<>(16, 0.75f, true);

    @Override
    public void onAccess(K key) {
        accessOrder.get(key);
    }

    @Override
    public void onPut(K key) {
        accessOrder.put(key, Boolean.TRUE);
    }

    @Override
    public K evict() {
        Map.Entry<K, Boolean> eldest = accessOrder.entrySet().iterator().next();
        K key = eldest.getKey();
        accessOrder.remove(key);
        return key;
    }

    @Override
    public void onRemove(K key) {
        accessOrder.remove(key);
    }
}
