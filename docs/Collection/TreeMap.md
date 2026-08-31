# TreeMap

## SECTION 1: WHAT IS TREEMAP (Simple Version)

Imagine a box where you can put in items with labels, but this box has a special rule: no matter what order you drop things in, whenever you look inside, everything is automatically arranged in sorted order. You don't sort it yourself — the box does it for you, every time you add or remove something.

That's a TreeMap. It's a collection that stores key-value pairs (like a dictionary — a word and its meaning), but unlike a plain unsorted collection, it always keeps the keys sorted. And you can ask it useful questions like "give me everything smaller than X" or "what's the closest key to Y" — things a plain HashMap can't answer efficiently.

**Banking Analogy:** A TreeMap is like a bank ledger of account balances that's always kept sorted by account number, no matter what order transactions come in. If an auditor asks "show me every account between 10001 and 10050," the ledger jumps straight to that range instantly — it doesn't scan every account from the start.

---

## SECTION 2: SYNTAX FUNDAMENTALS

### How to Declare It

```java
package org.ashish.learning.collections;

import java.util.Map;
import java.util.TreeMap;

Map<String, Double> accountBalances;
```

### How to Initialize It

```java
package org.ashish.learning.collections;

import java.util.Map;
import java.util.TreeMap;

public class TreeMapDemo {
    public static void main(String[] args) {
        // Natural ordering: String already implements Comparable,
        // so TreeMap knows how to sort these keys with no extra work
        Map<String, Double> accountBalances = new TreeMap<>();

        accountBalances.put("ACC1003", 45000.0);
        accountBalances.put("ACC1001", 120000.0);
        accountBalances.put("ACC1002", 78000.0);

        System.out.println(accountBalances);
        // Output: {ACC1001=120000.0, ACC1002=78000.0, ACC1003=45000.0}
        // Sorted automatically, even though we inserted 1003, 1001, 1002 in that order
    }
}
```

### Using a Custom Key (Comparable)

```java
package org.ashish.learning.collections;

import java.util.Map;
import java.util.TreeMap;

public class TreeMapCustomKeyDemo {
    public static void main(String[] args) {
        // Account implements Comparable<Account>
        // so TreeMap can use it as a key with ZERO extra setup
        Map<Account, String> accountNotes = new TreeMap<>();

        accountNotes.put(new Account("ACC1003", 45000.0), "Flagged for review");
        accountNotes.put(new Account("ACC1001", 120000.0), "VIP customer");
        accountNotes.put(new Account("ACC1002", 78000.0), "New account");

        System.out.println(accountNotes);
        // Sorted by whatever compareTo() says — accountId, per Account's natural order
    }
}
```

### Using a Comparator Instead

```java
package org.ashish.learning.collections;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class TreeMapWithComparatorDemo {
    public static void main(String[] args) {
        // Override Account's natural order (by ID) with balance-based order
        Map<Account, String> accountsByBalance =
            new TreeMap<>(Comparator.comparing(Account::getBalance));

        accountsByBalance.put(new Account("ACC1003", 45000.0), "Flagged for review");
        accountsByBalance.put(new Account("ACC1001", 120000.0), "VIP customer");
        accountsByBalance.put(new Account("ACC1002", 78000.0), "New account");

        System.out.println(accountsByBalance);
        // Now sorted by balance ascending, NOT by accountId — because we
        // passed a Comparator, so TreeMap ignores compareTo() entirely
    }
}
```

### Key Methods

- `put(key, value)` — inserts a key-value pair; if the key exists, updates the value
- `get(key)` — retrieves the value for a key, or `null` if not present
- `firstKey()` / `lastKey()` — returns the smallest / largest key in the map
- `headMap(key)` / `tailMap(key)` — sub-map of entries less than / greater than or equal to a given key
- `ceilingKey(key)` / `floorKey(key)` — smallest key ≥ given key, or largest key ≤ given key

### Rules You Must Know

1. **Keys must be `Comparable`, or you must supply a `Comparator`.** TreeMap needs to call `compareTo()` or `compare()` on every insert to find the right position — no rule, no way to place the key.
2. **`null` keys are NOT allowed** (with natural ordering). TreeMap can't call `null.compareTo(something)` — throws `NullPointerException` immediately. Contrast with HashMap, which allows one `null` key.
3. **Insertion order is irrelevant; sort order always wins.** Unlike LinkedHashMap, TreeMap discards insertion order entirely.
4. **Every operation costs O(log n), not O(1).** The price paid for automatic ordering and range queries, versus HashMap's average O(1).
5. **It's not thread-safe by default** — concurrent modification without synchronization can corrupt the tree structure or produce wrong results (race conditions).

---

## SECTION 3: DEEP DIVE + JVM PERSPECTIVE

### Part A: Internal Structure

TreeMap is backed by a **Red-Black Tree** — a self-balancing binary search tree (BST).

- In a BST, every node has at most two children; left child's key is smaller, right child's key is larger.
- A **plain BST can degrade into a straight line** if you insert already-sorted data (e.g., ACC1001, ACC1002, ACC1003 in order), turning O(log n) operations into O(n).
- A Red-Black Tree prevents this by coloring nodes red/black and applying rotation rules on every insert/delete, guaranteeing the tree never gets more than roughly 2x deeper on one side than the other — so operations stay O(log n) even in the worst case.
- **Why it exists:** HashMap gives fast lookups but zero ordering. A manually sorted array gives ordering but costs O(n) to insert into the middle. TreeMap gives O(log n) insert/delete/search AND ordered traversal — the best available trade-off when you need both speed and order.

### Part B: JVM Memory Layout

```
STACK (main thread)                      HEAP
+-------------------------+              +---------------------------------------+
| main()                  |              |  TreeMap object                       |
| +----------------------+|              |  +-----------------------------+     |
| | accountBalances  -----+|-------------->|  | root ------+                |     |
| | (reference)          ||              |  | comparator | (null = natural)|     |
| +----------------------+|              |  | size = 3   |                |     |
+-------------------------+              |  +------------+----------------+     |
                                          |               v                      |
                                          |      +-----------------+             |
                                          |      | Entry: ACC1002  |  (BLACK)    |
                                          |      | value = 78000.0 |             |
                                          |      | left --+  right +--+          |
                                          |      +--------+--------+--+          |
                                          |               v         v            |
                                          |   +-----------------+ +-------------------+
                                          |   | Entry: ACC1001  | | Entry: ACC1003    |
                                          |   | value=120000.0  | | value=45000.0     |
                                          |   | (RED)           | | (RED)             |
                                          |   +-----------------+ +-------------------+
                                          +---------------------------------------+

METHOD AREA (per-JVM, shared across threads)
+-----------------------------------------------------+
| TreeMap.class metadata:                              |
|  - vtable: put(), get(), firstKey(), ceilingKey()    |
|  - bytecode for insertion/rotation/rebalance logic   |
|  - static fields (if any)                            |
+-----------------------------------------------------+
```

**Reading this diagram:**
- The **stack** holds only `accountBalances`, a reference (8 bytes on a 64-bit JVM). It does NOT hold the actual data.
- The **heap** holds the `TreeMap` object, which holds a `root` reference to the top `Entry` node. Each `Entry` node is its own heap object, holding key, value, a color bit, and `left`/`right`/`parent` references — objects pointing to other objects, unlike ArrayList's contiguous array.
- The **method area** holds `TreeMap`'s bytecode once, shared by every instance.
- When you call `put("ACC1004", 30000.0)`, the JVM walks from `root`, comparing "ACC1004" against each `Entry`'s key — using either the key's own `compareTo()` or the supplied `Comparator`'s `compare()` — moving left or right until it finds the insertion spot, then possibly performs rotations to keep the tree balanced.

---

## FOLLOW-UP DOUBTS (Q&A)

### Q1: Does TreeMap itself implement Comparable or Comparator? How does it call compareTo internally, and how does that work?

**No — TreeMap does NOT implement `Comparable` or `Comparator` itself.** TreeMap is the **consumer** of those interfaces, not the provider. It's a sorting machine that *needs* a comparison rule fed into it — it doesn't have its own opinion on ordering.

What TreeMap actually holds internally (simplified from the real JDK source):

```java
public class TreeMap<K,V> {
    private Comparator<? super K> comparator;  // null if using natural ordering
    private Entry<K,V> root;
    private int size;
    // ...
}
```

When you write `new TreeMap<>()` with no arguments, `comparator` is set to `null`. When you write `new TreeMap<>(myComparator)`, it stores your comparator in that field.

Simplified internal logic of `put()`:

```java
public V put(K key, V value) {
    Entry<K,V> t = root;
    if (t == null) {
        root = new Entry<>(key, value, null);  // first node, no comparison needed
        size = 1;
        return null;
    }

    int cmp;
    Comparator<? super K> cpr = comparator;

    if (cpr != null) {
        // Path A: Comparator was supplied — use it
        cmp = cpr.compare(key, t.key);
    } else {
        // Path B: No Comparator — fall back to the key's own compareTo()
        Comparable<? super K> k = (Comparable<? super K>) key;  // unchecked cast!
        cmp = k.compareTo(t.key);
    }

    if (cmp < 0) {
        // go left in the tree
    } else if (cmp > 0) {
        // go right in the tree
    } else {
        // equal key found, replace value
    }
    // repeat this comparison at each node walked, until an empty spot is found
}
```

**Step by step, what happens when you call `accountBalances.put("ACC1004", 30000.0)`:**

```
1. TreeMap checks: is `comparator` field null?
     - If NO  -> call comparator.compare("ACC1004", currentNode.key)
     - If YES -> cast "ACC1004" to Comparable, call "ACC1004".compareTo(currentNode.key)

2. Start at root, get back -1/0/1

3. If negative -> move to root.left, repeat comparison there
   If positive -> move to root.right, repeat comparison there
   If zero     -> found existing key, overwrite its value, done

4. Repeat step 3 until you land on an empty spot (null child)
   -> that's where the new Entry node gets attached
```

This is exactly the `compareTo()`/`compare()` mechanism from the Comparable vs Comparator notes — TreeMap is the **caller**, walking down the tree, invoking it once per node visited, following left/right based on the sign of the result. This is also where the `ClassCastException` scenario comes from: if `cpr` is `null` AND the key doesn't actually implement `Comparable`, the cast on `(Comparable<? super K>) key` fails at runtime.

### Q2: How does the Red-Black Tree balance itself, and how does it differ from a plain BST? How does coloring work?

**The Coloring Rules (must hold at all times):**

1. Every node is either **RED** or **BLACK**.
2. The root is always **BLACK**.
3. Every leaf (conceptually, the `null` children) is treated as **BLACK**.
4. A **RED node cannot have a RED child** (no two reds in a row, parent-to-child).
5. Every path from a node down to any of its descendant null-leaves must pass through the **same number of BLACK nodes** ("black-height" rule).

Rule 5 is what mathematically guarantees the tree can never become badly lopsided. If one path had way more nodes than another but both need the *same black count*, the extra nodes on the longer path must alternate red/black, which bounds how much longer it can possibly be (at most 2x).

**Worked Example: Inserting ACC1001, ACC1002, ACC1003 in sorted order**

In a PLAIN BST (no balancing at all):

```
Insert ACC1001:          Insert ACC1002:          Insert ACC1003:

  ACC1001                  ACC1001                   ACC1001
                               \                          \
                             ACC1002                    ACC1002
                                                              \
                                                            ACC1003

This is now just a straight line (a "linked list in disguise").
Searching for ACC1003 requires visiting ALL 3 nodes = O(n) behavior.
With 1000 sequential account numbers, that's 1000 hops for the worst case.
```

In a RED-BLACK TREE, the same 3 insertions trigger a **rotation** to stay balanced:

```
Step 1: Insert ACC1001 (becomes root, must be BLACK per rule 2)

    ACC1001(B)


Step 2: Insert ACC1002 (goes right, colored RED per default rule for new nodes)

    ACC1001(B)
         \
      ACC1002(R)

(Still valid: no two reds in a row, black-height still consistent on all paths)


Step 3: Insert ACC1003 (goes right of ACC1002, would be RED)

    ACC1001(B)
         \
      ACC1002(R)
           \
        ACC1003(R)   <-- VIOLATION! Two reds in a row (ACC1002-R, ACC1003-R)
                          This also creates an unbalanced line, same problem as plain BST

    THE TREE DETECTS THIS VIOLATION AND FIXES IT WITH A LEFT ROTATION:

    Before rotation:          After rotation (ACC1002 becomes new subtree root):

    ACC1001(B)                       ACC1002(B)
         \                          /         \
      ACC1002(R)              ACC1001(R)   ACC1003(R)
           \
        ACC1003(R)
```

**What a rotation physically does:** it's not moving data around in an array — it's **rewiring which `Entry` object's `left`/`right`/`parent` references point to which other `Entry` objects**, plus flipping some color bits. `ACC1002` becomes the new middle/root of this subtree, with `ACC1001` as its left child and `ACC1003` as its right child — now the tree has proper branching instead of a straight line, and the tree stays at height ~log(n) no matter how many more sequential values get inserted.

### Q3: How do I make TreeMap thread-safe? What does a race condition look like in practice?

**Option A: `Collections.synchronizedSortedMap()`**

```java
package org.ashish.learning.collections;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ThreadSafeTreeMapDemo {
    public static void main(String[] args) {
        SortedMap<String, Double> accountBalances =
            Collections.synchronizedSortedMap(new TreeMap<>());

        // Every method call now acquires a lock before running,
        // so only ONE thread can modify the map at a time
        accountBalances.put("ACC1001", 50000.0);
    }
}
```

This wraps every method call in a `synchronized` block internally — effectively a single lock guarding the whole map. Simple, but threads queue up one at a time even for read operations, which can become a bottleneck under heavy concurrent load.

**Option B: `ConcurrentSkipListMap` (generally preferred for real systems)**

```java
package org.ashish.learning.collections;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.Map;

public class ConcurrentTreeMapDemo {
    public static void main(String[] args) {
        // Sorted, thread-safe, and doesn't force threads to queue behind one lock
        Map<String, Double> accountBalances = new ConcurrentSkipListMap<>();
        accountBalances.put("ACC1001", 50000.0);
    }
}
```

This uses a different internal data structure (a "skip list," not a Red-Black Tree) specifically designed to allow multiple threads to read and write concurrently with much finer-grained coordination — no single big lock.

**Concrete Race Condition Example**

Imagine two threads both handling a withdrawal from the same `TreeMap<String, Double> accountBalances`, for the same account "ACC1001", which currently has balance 5000:

```
UNSYNCHRONIZED TreeMap — two threads racing:

Thread A (mobile app withdrawal of 3000)     Thread B (ATM withdrawal of 3000)
--------------------------------------       --------------------------------------
1. read balance = 5000
                                              1. read balance = 5000
2. check: 3000 <= 5000? YES, proceed
                                              2. check: 3000 <= 5000? YES, proceed
3. balance = 5000 - 3000 = 2000
   put("ACC1001", 2000)
                                              3. balance = 5000 - 3000 = 2000
                                                 put("ACC1001", 2000)

FINAL BALANCE STORED: 2000

But 6000 total was withdrawn from an account that only had 5000!
Both threads read the SAME starting balance before either one wrote back,
so neither saw the other's update — this is called a "lost update."
```

This is a **race condition**: the final, correct outcome depends on the unpredictable timing/interleaving of the two threads, and here it produces a result that's flat-out wrong — the bank just lost 1000 with no record of where it went. Beyond wrong values, an unsynchronized TreeMap's `put()` involves multiple steps (walk the tree, possibly rotate nodes, rewire references) — if Thread A is mid-rotation while Thread B starts reading, you can get a genuinely corrupted tree structure (not just wrong values, but a broken data structure), which can cause crashes or infinite loops during traversal.

Using `synchronizedSortedMap` or `ConcurrentSkipListMap` prevents this by ensuring the read-check-write sequence for a withdrawal completes without another thread interleaving in the middle.

### Q4: Explain the Method Area diagram line-by-line

```
METHOD AREA (per-JVM, shared across threads)
+-----------------------------------------------------+
| TreeMap.class metadata:                              |
|  - vtable: put(), get(), firstKey(), ceilingKey()    |
|  - bytecode for insertion/rotation/rebalance logic   |
|  - static fields (if any)                            |
+-----------------------------------------------------+
```

**"per-JVM, shared across threads"** — When your Java program starts, the JVM creates exactly ONE Method Area (part of what's now called "Metaspace" in modern JVMs) for the entire application. Every thread you spin up — whether `main()` or extra worker threads — reads from this SAME shared area. This is different from the stack, where each thread gets its own separate stack.

**"TreeMap.class metadata"** — When your program first uses the `TreeMap` class, the JVM's class loader reads the compiled `TreeMap.class` bytecode file and loads its structural information into the Method Area exactly ONCE — no matter how many `TreeMap` objects you later create with `new TreeMap<>()`. Think of it as a blueprint stored in a shared filing cabinet — every actual TreeMap object you build on the heap just references this one shared blueprint.

**"vtable: put(), get(), firstKey(), ceilingKey()"** — The vtable (virtual method table) is a lookup list: "if someone calls `.put()` on a TreeMap object, here's the actual memory address of the compiled instructions for that method." When you write `accountBalances.put("ACC1004", 30000.0)`, the JVM follows: `accountBalances` (heap object) → type pointer → `TreeMap.class` entry in the Method Area → vtable → jumps to the exact bytecode location for `put()`. A thousand different `TreeMap` objects on the heap all point to this ONE vtable, because they all run the exact same `put()` code, just on their own individual data.

**"bytecode for insertion/rotation/rebalance logic"** — The actual compiled instructions (JVM bytecode, not source code) for walking the tree, comparing keys, performing rotations, flipping colors. Identical for every TreeMap instance — only the *data* (the actual `Entry` nodes with your account keys/values) differs, and that data lives separately, out on the heap.

**"static fields (if any)"** — If `TreeMap` declared any `static` fields (shared constants belonging to the class itself, not to individual instances), those would also live here in the Method Area, since `static` fields are "one copy per class," not "one copy per object."

**Why this separation matters practically:** it's why creating a thousand `TreeMap` objects doesn't cost a thousand copies of `put()`'s logic — that code is loaded once, in the Method Area, and every object instance on the heap just borrows it via the vtable pointer. Only the actual account data multiplies per object; the "how to insert/balance" instructions do not.

---

## Banking-Context Worked Example

**Scenario:** A fraud-monitoring module needs a live, sorted ledger of transaction amounts flagged as suspicious today, and must quickly answer "show me all flagged transactions above ₹1,00,000."

```java
package org.ashish.learning.collections;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class FraudFlaggedTransactions {
    public static void main(String[] args) {
        Map<Double, String> flaggedTransactions = new TreeMap<>();

        flaggedTransactions.put(45000.0, "TXN-9001");
        flaggedTransactions.put(250000.0, "TXN-9002");
        flaggedTransactions.put(99999.0, "TXN-9003");
        flaggedTransactions.put(105000.0, "TXN-9004");
        flaggedTransactions.put(500000.0, "TXN-9005");

        TreeMap<Double, String> sortedFlags = (TreeMap<Double, String>) flaggedTransactions;

        SortedMap<Double, String> highValueFlags = sortedFlags.tailMap(100000.0);
        System.out.println("Transactions >= 1,00,000: " + highValueFlags);
        // Output: {105000.0=TXN-9004, 250000.0=TXN-9002, 500000.0=TXN-9005}

        System.out.println("Highest risk transaction: " + sortedFlags.lastEntry());
        // Output: 500000.0=TXN-9005

        Double closestMatch = sortedFlags.ceilingKey(100000.0);
        System.out.println("Closest flag at or above 1,00,000: " + closestMatch);
        // Output: 105000.0
    }
}
```

---

## Common Pitfalls

1. **Forgetting `Comparable`/`Comparator` until runtime.** Compiles fine, crashes on the *second* insert with `ClassCastException` — because the first key just becomes `root` with nothing to compare against yet.
2. **Assuming `null` keys work like in HashMap.** They throw `NullPointerException` immediately with natural ordering.
3. **Mutating a key after inserting it.** If a custom key's `compareTo()`-relevant field changes after insertion, the tree's internal ordering breaks — future lookups can silently fail to find it.
4. **Using TreeMap when you don't need ordering.** Paying O(log n) instead of HashMap's O(1) for nothing if you never use sorted iteration or range queries.
5. **Forgetting a `Comparator` overrides `compareTo()` entirely.** If you pass a `Comparator` to the constructor, TreeMap uses it exclusively — even if the key also implements `Comparable`.
6. **Assuming TreeMap is thread-safe by default.** It isn't — concurrent unsynchronized access can produce lost updates or corrupt the tree structure. Use `Collections.synchronizedSortedMap()` or `ConcurrentSkipListMap` for concurrent access.

---

## Comparison Table: TreeMap vs HashMap vs LinkedHashMap

| Feature | HashMap | LinkedHashMap | TreeMap |
|---|---|---|---|
| Ordering | None (unpredictable) | Insertion order (or access order) | Sorted by key (natural or Comparator) |
| Backing structure | Array of buckets + linked list/tree per bucket | HashMap + doubly linked list | Red-Black Tree |
| `get`/`put` average time | O(1) | O(1) | O(log n) |
| `null` keys | One `null` key allowed | One `null` key allowed | Not allowed (natural ordering) |
| Range queries | Not supported | Not supported | Supported (`headMap`, `tailMap`, `ceilingKey`) |
| Memory overhead per entry | Lowest | Medium | Highest |
| Best banking use case | Fast account-number lookup, no ordering needed | Recently-accessed accounts cache (LRU-style) | Sorted transaction ledger, range-based fraud checks |

---

## Revision Checklist

- [ ] Can explain TreeMap in plain English + banking analogy
- [ ] Can declare and initialize a TreeMap with String keys (natural ordering)
- [ ] Can use a custom `Comparable` class as a TreeMap key with no extra setup
- [ ] Can override that natural order using a `Comparator` passed to the constructor
- [ ] Can explain why TreeMap does NOT implement Comparable/Comparator itself, and how it calls compareTo()/compare() internally during put()
- [ ] Can explain why `ClassCastException` happens only on the 2nd insert, not the 1st
- [ ] Can state all 5 Red-Black Tree coloring rules from memory
- [ ] Can draw a rotation example (like ACC1001/1002/1003) showing before/after and why it was triggered
- [ ] Can explain why plain BSTs degrade to O(n) with sorted input and how Red-Black Trees prevent it
- [ ] Can explain how to make TreeMap thread-safe (two approaches) and describe a concrete race condition
- [ ] Can explain what lives in the Method Area vs the Heap vs the Stack for a TreeMap, including the role of the vtable
- [ ] Can use `tailMap`, `ceilingKey`, `firstKey`/`lastKey` in a worked example
- [ ] Can state TreeMap vs HashMap vs LinkedHashMap trade-offs without the table
