package com.example.cache;

public interface EvictionPolicyFactory<K> {
    EvictionPolicy<K> create();
}
