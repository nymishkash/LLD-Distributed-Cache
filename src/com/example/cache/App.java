package com.example.cache;

public class App {

    public static void main(String[] args) {
        // Seed a fake database
        FakeDatabase db = new FakeDatabase();
        db.seed("user:1", "Alice");
        db.seed("user:2", "Bob");
        db.seed("user:3", "Charlie");
        db.seed("user:4", "Diana");
        db.seed("user:5", "Eve");

        // 3 nodes, capacity 2 each, modulo distribution, LRU eviction
        DistributedCache<String, String> cache = new DistributedCache<>(
                3, 2,
                new ModuloDistribution(),
                LRUEvictionPolicy::new,
                db
        );

        System.out.println("=== Distributed Cache Demo ===\n");

        // Cache misses — fetches from DB
        System.out.println("get(user:1) = " + cache.get("user:1"));
        System.out.println();
        System.out.println("get(user:2) = " + cache.get("user:2"));
        System.out.println();

        // Cache hit
        System.out.println("get(user:1) = " + cache.get("user:1"));
        System.out.println();

        // Direct put
        cache.put("user:6", "Frank");
        System.out.println();

        // Fill up nodes to trigger eviction
        System.out.println("get(user:3) = " + cache.get("user:3"));
        System.out.println();
        System.out.println("get(user:4) = " + cache.get("user:4"));
        System.out.println();
        System.out.println("get(user:5) = " + cache.get("user:5"));
        System.out.println();

        // This may trigger eviction on a full node
        cache.put("user:7", "Grace");
        System.out.println();

        // Verify evicted key causes a cache miss
        System.out.println("get(user:2) = " + cache.get("user:2"));
    }
}
