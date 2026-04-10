package com.example.cache;

import java.util.HashMap;
import java.util.Map;

public class FakeDatabase implements Database<String, String> {
    private final Map<String, String> data = new HashMap<>();

    public void seed(String key, String value) {
        data.put(key, value);
    }

    @Override
    public String get(String key) {
        System.out.println("[DB] fetching key: " + key);
        return data.get(key);
    }
}
