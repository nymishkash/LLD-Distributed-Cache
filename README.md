# LLD-Distributed-Cache

Distributed cache system with pluggable distribution strategies and eviction policies.

## Build & Run

```bash
cd src
javac com/example/cache/*.java
java com.example.cache.App
```

## Design

### Class Diagram

```
<<interface>>                    <<interface>>
DistributionStrategy             EvictionPolicy<K>
+ getNodeIndex(key, nodeCount)   + onAccess(key)
        ^                        + onPut(key)
        |                        + evict(): K
ModuloDistribution               + onRemove(key)
                                        ^
                                        |
                                 LRUEvictionPolicy<K>

<<interface>>
EvictionPolicyFactory<K>
+ create(): EvictionPolicy<K>

<<interface>>
Database<K, V>              CacheNode<K, V>
+ get(key): V               - storage: HashMap<K, V>
        ^                   - evictionPolicy: EvictionPolicy<K>
        |                   + get(key): V
FakeDatabase                + put(key, value)

DistributedCache<K, V>
- nodes: List<CacheNode<K, V>>
- strategy: DistributionStrategy
- database: Database<K, V>
+ get(key): V
+ put(key, value)
```

### Key Design Decisions

1. **Pluggable distribution strategy** -- `DistributionStrategy` interface with a single method `getNodeIndex(key, nodeCount)`. Currently uses modulo-based hashing. Consistent hashing can be added by implementing this interface without changing any other code.

2. **Pluggable eviction policy** -- `EvictionPolicy<K>` interface + `EvictionPolicyFactory<K>` factory pattern. Each `CacheNode` gets its own eviction policy instance. Currently implements LRU via `LinkedHashMap` with access-order mode. MRU or LFU can be swapped in by providing a different factory.

3. **Cache miss handling** -- On a `get()` miss, `DistributedCache` fetches from the backing `Database`, stores the result in the appropriate cache node, and returns it. This is transparent to the caller.

4. **Thread safety** -- `CacheNode` methods are `synchronized` to handle concurrent access safely.

5. **Generics** -- Full `<K, V>` parameterization so the cache works with any key/value types.

### How It Works

- **Data distribution**: `hash(key) % nodeCount` determines which node stores a key. All operations for that key route to the same node.
- **Eviction**: When a node is full, the eviction policy picks a victim key to remove before inserting the new entry. LRU evicts the least recently accessed key.
- **Extensibility**: Add a new distribution strategy or eviction policy by implementing the corresponding interface -- no changes to `DistributedCache` or `CacheNode`.
