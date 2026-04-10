package com.example.cache;

public interface Database<K, V> {
    V get(K key);
}
