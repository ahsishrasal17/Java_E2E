# Day 2 (Collections) — LinkedHashMap

> Continuation of Day 2, following HashMap internals (treeification etc.). Covers LinkedHashMap internals, LRU cache design, and LeetCode 146.

---

## 1. Syntax & Rules

```java
// Default constructor — maintains INSERTION order
LinkedHashMap<String, Account> map1 = new LinkedHashMap<>();

// 3-arg constructor — the one that matters for LRU caches
// accessOrder = true -> maintains ACCESS order (most recently accessed goes last)
LinkedHashMap<String, Account> map2 = new LinkedHashMap<>(16, 0.75f, true);

// The other override that matters — auto-eviction hook
LinkedHashMap<String, Account> lru = new LinkedHashMap<>(16, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Account> eldest) {
        return size() > 100; // auto-evict oldest when size exceeds 100
    }
};
```

**Rules to internalize:**
- `LinkedHashMap<K,V> extends HashMap<K,V>` — it's not a separate data structure, it's HashMap **plus** bookkeeping.
- Default iteration order = **insertion order**. Unlike HashMap, where iteration order is undefined (bucket-index-dependent, looks random).
- With `accessOrder = true`, every `get()` call moves that entry to the end of the ordering — this is the mechanic that makes LRU caches possible.
- `removeEldestEntry()` is a hook method that returns `false` by default (never auto-evict). Override it, and LinkedHashMap will call it automatically after every `put()`.
- `put()` always **inserts first, then asks `removeEldestEntry()` afterward**. Size can briefly exceed capacity by 1 inside a single `put()` call, before self-correcting.

### Declaring with `LinkedHashMap` vs `Map`

Normal rule: declare with the interface type (`Map`), instantiate with the concrete type.

```java
Map<String, Account> map1 = new HashMap<>(); // normal usage
```

**Exception**: when you rely on `LinkedHashMap`-specific behavior (ordering, `removeEldestEntry`), you must **declare** the variable as `LinkedHashMap` — `Map` and even `HashMap` make no order guarantees in their contract, and you can't call an override like `removeEldestEntry` through a `Map` reference.

```java
LinkedHashMap<String, Account> map1 = new LinkedHashMap<>(); // correct when order/LRU matters
```

Rule of thumb: declare as the **most general type that still preserves the guarantees you're relying on**.

### Generics can't use primitives

`LinkedHashMap<int, int>` is illegal — generic type parameters must be reference types. Use wrapper classes (`Integer`, `Boolean`) instead; Java auto-boxes/unboxes behind the scenes.

---

## 2. Plain-Language Analogy

A **HashMap is a warehouse with numbered bins** (buckets) — fast to find things, but no memory of what order you put them in.

**LinkedHashMap is that same warehouse, but someone's also keeping a clipboard** — a running list threading through every item in the order it arrived (or was last touched). The bins give O(1) lookup; the clipboard gives predictable order when walking through everything.

---

## 3. Deep Dive — Banking Domain

**a) Ordered API response** — building `GET /accounts/{id}/recent-transactions`, inserting transactions in DB query order (already sorted by timestamp). A `HashMap` would scramble this order on serialization (order depends on `hashCode()`/bucket placement, not insertion). A `LinkedHashMap` preserves insertion order automatically — no extra sort needed downstream.

**b) LRU cache for account lookups** — a service layer caching recently-viewed `Account` objects to cut DB hits, capped at N entries, evicting the *least recently used* (not oldest inserted) when full. `accessOrder=true` + `removeEldestEntry` gives this directly:

```java
public class AccountCache extends LinkedHashMap<String, Account> {
    private static final int MAX_ENTRIES = 1000;

    public AccountCache() {
        super(16, 0.75f, true); // accessOrder = true
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Account> eldest) {
        return size() > MAX_ENTRIES;
    }
}
```

Every `cache.get(accountId)` silently promotes that entry to "most recently used." Once over `MAX_ENTRIES`, the least recently accessed entry is evicted automatically on the next `put()`.

---

## 4. What is an LRU Cache?

**LRU = Least Recently Used** — a cache eviction policy: when full, evict the item that hasn't been *accessed* (read or written) for the longest time.

Banking example: `AccountService` caches `Account` objects, capped at 1000. When account #1001 needs caching, LRU evicts whichever account nobody has looked up in the longest time — likely dormant, not actively queried.

Contrast (awareness only):
- **FIFO** — evict oldest *inserted*, regardless of access frequency since.
- **LFU** (Least Frequently Used) — evict lowest access *count*.

---

## 5. JVM-Level Perspective

- Every `LinkedHashMap.Entry<K,V>` **extends** `HashMap.Node<K,V>` and adds two extra pointers: `before` and `after`.

```java
// HashMap.Node — basic building block
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;  // collision chaining within a bucket
}

// LinkedHashMap.Entry — extends HashMap.Node
static class Entry<K,V> extends HashMap.Node<K,V> {
    Entry<K,V> before, after;  // ordering pointers
}
```

- So **each entry object lives in two structures simultaneously** — no duplication, just extra pointers on the same object:
  - The **bucket array** (`table[]`, inherited from HashMap) — for O(1) hash-based lookup. Position determined by `hash(key) % table.length`.
  - A **doubly-linked list** threading through all entries in order — maintained via `before`/`after` pointers, with `head`/`tail` references held by the map itself. Position determined purely by insertion/access time.
- `get()` with `accessOrder=true` internally calls `afterNodeAccess(node)` — unlinks the node from its current linked-list position and relinks it at the tail. **The bucket array position never changes** — only the linked-list position does. No rehash cost for reordering.
- **Iteration** (`entrySet()`, `keySet()`, `values()`) — defined on the `Map` interface, so every implementation (HashMap, LinkedHashMap, TreeMap) has these methods. What differs is *which structure the iterator walks*:
  - `HashMap.entrySet()` → walks the **bucket array**, `table[0] → table[1] → ...`, following `next` chains for collisions. Order looks arbitrary — depends on hash values, not insertion.
  - `LinkedHashMap.entrySet()` → walks the **doubly-linked list**, `head → ... → tail`, via `after` pointers. Order is predictable (insertion or access order).
  - `TreeMap.entrySet()` → walks in sorted key order (covered separately).

### Diagram: dual structure

```
Bucket array (hash-based lookup, HashMap.Node):
table[0] -> empty
table[1] -> EntryA
table[2] -> EntryC -> EntryD   (collision chain via `next`)
table[3] -> EntryB

Doubly-linked list (insertion/access order, before/after pointers):
head <-> EntryA <-> EntryB <-> EntryC <-> EntryD <-> tail
```

Note `EntryB` sits in `table[3]` (bucket array) but is 2nd in the linked list (inserted 2nd) — same object, reachable both ways, positions in each structure are completely independent.

### Load factor vs. cache capacity — two unrelated knobs

- **Your `capacity` field** (e.g. `5` in `AccountCache`) — your own logic, enforced via `removeEldestEntry`. Caps the *number of entries*.
- **`loadFactor`** — governs when the internal `table[]` bucket array **resizes** (doubles). `new AccountCache(5, 0.75f, true)` rounds the initial table to the next power of two (8), resize threshold = `8 × 0.75 = 6`.
- For a fixed-capacity LRU cache, entry count hovers right at `capacity` (briefly touching `capacity+1` mid-`put()`), so resizing rarely if ever triggers. These are separate, unrelated concerns that happen to share the word "capacity."

---

## 6. Hands-on: AccountCache (LRU via LinkedHashMap)

```java
package org.ashish.learning.Collection.Map.LinkedHashMap;

import java.util.LinkedHashMap;
import java.util.Map;

class Account {
    private final String accountId;
    private double balance;

    public Account(String accountId, double balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    @Override
    public String toString() {
        return "Account{" + accountId + ", balance=" + balance + "}";
    }
}

public class AccountCache extends LinkedHashMap<String, Account> {

    private final int capacity;

    public AccountCache(int capacity, float loadFactor, boolean accessOrder) {
        super(capacity, loadFactor, accessOrder);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Account> eldest) {
        return size() > capacity;
    }

    public static void main(String[] args) {
        AccountCache cache = new AccountCache(5, 0.75f, true);

        cache.put("ACC001", new Account("ACC001", 1000.0));
        cache.put("ACC002", new Account("ACC002", 2000.0));
        cache.put("ACC003", new Account("ACC003", 3000.0));
        cache.put("ACC004", new Account("ACC004", 4000.0));
        cache.put("ACC005", new Account("ACC005", 5000.0));

        System.out.println("Before ACC006 insert: " + cache.keySet());

        cache.get("ACC001"); // promotes ACC001 to most-recently-used

        cache.put("ACC006", new Account("ACC006", 6000.0)); // triggers eviction

        System.out.println("After ACC006 insert: " + cache.keySet());
    }
}
```

**Expected output:**
```
Before ACC006 insert: [ACC002, ACC003, ACC004, ACC005, ACC001]
After ACC006 insert: [ACC003, ACC004, ACC005, ACC001, ACC006]
```

`ACC002` is evicted (least recently used — never accessed after insertion). `ACC001` survives despite being inserted first, because it was accessed before the 6th insert — proving this is access-based (LRU), not insertion-based (FIFO).

---

## 7. LeetCode 146 — LRU Cache

**Problem**: Design `LRUCache` with `get(key)` / `put(key, value)`, both O(1) average, evicting least recently used key when capacity is exceeded.

### Option 1 — using LinkedHashMap directly

```java
import java.util.LinkedHashMap;
import java.util.Map;

class LRUCache extends LinkedHashMap<Integer, Integer> {

    private final int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true); // accessOrder = true
        this.capacity = capacity;
    }

    public int get(int key) {
        return super.getOrDefault(key, -1);
    }

    public void put(int key, int value) {
        super.put(key, value);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        return size() > capacity;
    }
}
```

Notes:
- `get()` overridden to return `-1` (not `null`) for missing keys — `getOrDefault` avoids a `NullPointerException` from unboxing `null` into `int`.
- Satisfies all constraints, but only demonstrates *using* an LRU structure, not understanding *how* one is built. Interviewers often disallow this.

### Option 2 — built from scratch (HashMap + custom doubly-linked list)

The "real" exercise — replicate what LinkedHashMap does internally, by hand.

```java
import java.util.HashMap;
import java.util.Map;

class LRUCache {

    class Node {
        int key;
        int value;
        Node prev;
        Node next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final Map<Integer, Node> map;
    private final int capacity;
    private final Node head; // sentinel: head.next = most recently used
    private final Node tail; // sentinel: tail.prev = least recently used

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        head = new Node(-1, -1);
        tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
    }

    private void addToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToFront(Node node) {
        remove(node);
        addToFront(node);
    }

    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1;
        }
        Node node = map.get(key);
        moveToFront(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value;
            moveToFront(node);
            return;
        }

        Node newNode = new Node(key, value);
        map.put(key, newNode);
        addToFront(newNode);

        if (map.size() > capacity) {
            Node lru = tail.prev;
            remove(lru);
            map.remove(lru.key);
        }
    }
}
```

Why each piece exists:
- **Sentinel `head`/`tail`** — fake boundary nodes that never hold real data, so `addToFront`/`remove` never need null-checks for "is this the first/last node?"
- **`map` stores `Node` references** — makes `get()` O(1): hash straight to the node, no list traversal.
- **All operations O(1)** — hashmap lookup O(1); pointer rewiring touches a fixed number of pointers regardless of list size.
- **`Node` stores `key`, not just `value`** — needed when evicting `tail.prev`, to know which key to remove from `map`.

Structurally identical to LinkedHashMap's internals (§5) — same `before`/`after`-style pointer mechanics, `head`/`tail` bookkeeping, just hand-built instead of inherited from the JDK.

---

## 8. Q&A Log (questions asked during this session)

**Q: Why `LinkedHashMap<String, Account> map1 = new LinkedHashMap<>();` and not `Map<String, Account> map1 = new LinkedHashMap<>();`?**
A: See §1 "Declaring with LinkedHashMap vs Map" — declare as `LinkedHashMap` when relying on order/LRU-specific behavior not exposed by the `Map` contract.

**Q: What is an LRU cache?**
A: See §4.

**Q: What does "textbook way to implement LRU without hand-rolling a doubly-linked list yourself" mean — how does LinkedHashMap handle it?**
A: Without LinkedHashMap, you'd build a `HashMap<K,Node>` + your own doubly-linked list by hand (~60-80 lines — see §7 Option 2). LinkedHashMap already has that linked list built in (`before`/`after` pointers), so `accessOrder=true` + `removeEldestEntry` override gives you the same behavior in ~10 lines.

**Q: What is `LinkedHashMap.Entry` and `HashMap.Node`?**
A: See §5 — `HashMap.Node` is the base node (hash, key, value, `next` for collision chain); `LinkedHashMap.Entry extends HashMap.Node`, adding `before`/`after` ordering pointers.

**Q: If `get()` moves an entry to the end, when cleanup happens at capacity+1, is it removed from the start?**
A: Yes — `head` = least recently used (front), `tail` = most recently used (back). New/recently-accessed entries move to the tail; eviction always removes from the head.

**Q: So are there 2 entries (re: bucket array + linked list)?**
A: No — one entry object, participating in two structures simultaneously via extra pointer fields (`next` for bucket chain, `before`/`after` for ordering list). See §5 diagram.

**Q: What are `entrySet()`, `keySet()` — are these only in LinkedHashMap or also HashMap?**
A: Defined on the `Map` interface — every implementation has them. What differs is which internal structure the iterator walks (bucket array vs. linked list vs. sorted tree). See §5.

**Q: [After diagram] Please explain with a diagram how LinkedHashMap contains Node and Entry both, and how entrySet/keySet iteration differs between HashMap (bucket) and LinkedHashMap (linked list).**
A: See §5 diagram and iteration bullet points.

**Q: In the AccountCache constructor, can we change `loadFactor`? What if `accessOrder` is passed as `false`?**
A: `loadFactor` can be changed but barely matters for a fixed-capacity LRU cache (see §5, load factor vs. capacity). `accessOrder=false` turns the cache into FIFO-by-insertion — `get()` no longer promotes entries, so accessing an entry won't save it from eviction.

**Q: `cache` is used as an object with `.put()` — but is `cache` a `LinkedHashMap` object? How does `put` work?**
A: Yes — `cache` is an `AccountCache`, which *is-a* `LinkedHashMap<String, Account>` via inheritance, so it has all inherited methods (`put`, `get`, `keySet`, etc.). `capacity` (an `int` field) and `cache` (the map object) are unrelated things that got conflated.

**Q: Explain `removeEldestEntry(Map.Entry<String, Account> eldest)` and what `Map.Entry<String, Account>` is.**
A: See §5/§6 — hook method called automatically by `LinkedHashMap` after every `put()`; `Map.Entry` is the inner interface representing a single key-value pair, implemented by `HashMap.Node`/`LinkedHashMap.Entry` under the hood, exposing `getKey()`/`getValue()`.

**Q: Why does the map never grow past capacity+1? With capacity=5 and size=5, how does a 6th element even fit?**
A: `put()` always inserts first, then calls `removeEldestEntry()` afterward — size briefly touches 6, then self-corrects back to 5 within the same `put()` call. See §5 "put() always inserts first."

**Q: As soon as the map fills to 75% (load factor), does the bucket array size not get doubled?**
A: Yes, in principle — but this is a separate mechanism from your `capacity` eviction logic. See §5 "Load factor vs. cache capacity."

---

## Next up

- TreeMap (Red-Black tree ordering, `NavigableMap`, `Comparable`/`Comparator`) — to complete the Map family before Day 3.
- Day 3: Exceptions & Generics.

## Streak checklist for this session

- [ ] Push `AccountCache.java` + `LRUCache.java` (both options) to GitHub (`Java_Itinerary` repo)
- [ ] LeetCode 146 marked solved
- [ ] This file committed as `docs/Collections/09_LinkedHashMap.md`
