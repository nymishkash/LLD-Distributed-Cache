package com.example.cache;

public interface EvictionPolicy<K> {
    void onAccess(K key);
    void onPut(K key);
    K evict();
    void onRemove(K key);
}
