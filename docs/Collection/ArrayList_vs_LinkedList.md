# ArrayList vs LinkedList (Day 2)

## 1. What They Are (Simple Version)

**`ArrayList` = numbered parking spots in a single row.** Every spot has a fixed number (index) — walk directly to spot #47, no searching needed. But if a car needs to park in the middle of a full row, every car after that spot must shift over by one — expensive.

**`LinkedList` = a treasure hunt / chain of sticky notes.** Each note points to where the next one is. To find the 47th note, you must start at note #1 and follow the chain — no shortcut. But inserting a new note in the middle just means tearing the chain at one point and re-pointing two neighbors — no need to touch any other note.

### Banking angle
- **`ArrayList`** suits `List<Transaction>` where you frequently jump to a specific position ("show me the 50th most recent transaction") or iterate in order — random access is fast.
- **`LinkedList`** suits a processing queue (e.g. pending fraud-review cases) where items are added at the back and removed from the front constantly, and you rarely need to jump to "the 47th case in line."

---

## 2. Syntax Fundamentals

```java
List<String> arrayList = new ArrayList<>();
List<String> linkedList = new LinkedList<>();
```
Both implement `List` — identical external methods (`add()`, `get(index)`, `remove()`, `size()`). The difference is entirely internal.

### Rules
1. `ArrayList` is backed by a dynamically resizable array (`Object[]`) internally. `LinkedList` is backed by a **doubly linked list** of `Node` objects, each holding data plus pointers to the previous and next node.
2. `ArrayList` has a **capacity** (allocated space) separate from its **size** (actual element count). When size exceeds capacity, the array automatically resizes — invisible to the caller, but has real performance cost.
3. `LinkedList` implements **both `List` AND `Deque`** — usable as a queue/stack too, via `addFirst()`, `addLast()`, `removeFirst()`, `removeLast()`, on top of standard `List` methods.

### Correction on declaring by interface — using `Deque`, not `LinkedList`
`addFirst()`/`removeFirst()`/etc. are NOT part of `List` — they belong to the **`Deque`** interface, which `LinkedList` also implements. If you need those methods, the correct "program to an interface" approach is:
```java
Deque<String> queue = new LinkedList<>(); // Deque interface exposes addFirst/removeFirst etc.
```
Declaring as `LinkedList<String> queue = ...` works but breaks the interface-first principle — `Deque` is the right interface for this need, not dropping to the concrete class.

---

## 3. How an Array Actually Works (the foundation under ArrayList)

An array is a **contiguous block of memory** — all elements stored back-to-back with no gaps. `new int[5]` reserves one continuous memory chunk sized for exactly 5 elements.

**Why `get(index)` is O(1):** the JVM computes the exact memory address directly:
```
address_of_element[i] = base_address + (i × size_of_each_element)
```
No searching or walking — just arithmetic, then a direct jump. This is why array access is instant regardless of array size.

For `Object[]` (backing `ArrayList<String>`), each slot stores a **reference** (memory address, not the actual object), pointing to the real object wherever it lives on the heap.

---

## 4. Deep Dive + JVM Perspective

### `ArrayList` — memory layout and resizing

```
ArrayList object (heap)              backing Object[] array (heap)
+----------------------+       +----+----+----+------+
| size = 3               |       | [0]| [1]| [2]| [3]  |
| array reference ---------->   | ptr| ptr| ptr| empty|
+----------------------+       +----+----+----+------+
                                  ↓     ↓     ↓
                              "Ashish" "Priya" "Rahul"  (actual objects, elsewhere on heap)
```

**Growth mechanism:** arrays in Java have a **fixed size once created** — they cannot be resized in place. When `add()` is called and the array is full: (1) a **brand-new, larger array** is created (typically ~1.5× the old capacity), (2) every existing reference is **copied** into the new array, (3) the new element is added, (4) the old array becomes eligible for garbage collection.

This copy is **O(n)**. This is why `ArrayList.add()` is described as **"amortized O(1)"** — most calls are instant, but occasionally one call triggers an expensive O(n) resize. Averaged over many calls, it works out to constant time per call — but any single call can occasionally spike.

### `LinkedList` — memory layout

```
LinkedList object                Node A              Node B              Node C
+----------------+          +-----------+       +-----------+       +-----------+
| first -----------------> | prev: null |       | prev: <---|       | prev: <---|
| last  ------------------>| data: "Ashish"|<--->| data: "Priya"|<-->| data: "Rahul"|
| size = 3         |       | next: ---->|       | next: ---->|      | next: null |
+----------------+          +-----------+       +-----------+       +-----------+
```

Each `Node` is a **separate heap object** — not contiguous in memory, connected only by pointers. The `LinkedList` object tracks `first` and `last`.

**Why `get(index)` is O(n):** no way to "jump" to node #47 — must start at `first` (or `last`) and follow `next`/`prev` pointers one node at a time until reaching the target.

**Optimization Java actually uses:** if the index is in the first half of the list, traversal starts from `first`; if in the second half, starts from `last` and walks backward. Roughly halves average traversal distance — but still fundamentally O(n), just a smaller constant factor.

**Why insertion/removal at a KNOWN position is O(1) once there:** inserting between A and B only requires re-pointing `A.next` and `B.prev` — nothing else shifts, unlike `ArrayList`'s physical shifting of every subsequent element.

**Important nuance:** `LinkedList.add(index, value)` is still O(n) overall in practice, because Java must first traverse to find that index (O(n)) before the O(1) insertion. The "O(1) insertion" benefit is only fully realized when you're already holding a reference to the right position (iterator, or specifically `addFirst()`/`addLast()`) — not when inserting by a numeric index from scratch.

### Does `LinkedList` store actual data or references?
**References too — but with an extra layer of indirection.** Each `Node` holds a reference to the actual data object (not the data inline), plus references to neighboring nodes. Two hops to reach data: `LinkedList → Node → actual data object`, versus `ArrayList`'s single hop: `ArrayList → array slot → actual data object`. This extra indirection is part of why `LinkedList` has higher memory overhead per element — every element costs a whole extra `Node` object (object header + two pointer fields) beyond just the reference to the data.

---

## 5. Time Complexity Summary

| Operation | `ArrayList` | `LinkedList` |
|---|---|---|
| `get(index)` | O(1) — direct offset | O(n) — must walk the chain |
| `add()` at end | Amortized O(1) | O(1) |
| `add()` at beginning/middle by index | O(n) — must shift elements | O(n) overall — traversal to find index dominates, even though the actual splice is O(1) |
| `remove()` from beginning/middle by index | O(n) — must shift elements | O(n) overall, same caveat |
| `addFirst()`/`addLast()` (Deque methods, no index search) | N/A (not O(1) at front for ArrayList) | True O(1) — no traversal needed |
| Memory overhead | Lower — array + some empty capacity | Higher — every element needs extra prev/next pointers + Node object overhead |

**Measured proof (own benchmark, 100,000 insertions at index 0):**
```
ArrayList.add(0, i) × 100,000:  472 ms
LinkedList.add(0, i) × 100,000:  16 ms
```
`ArrayList.add(0, ...)` repeatedly is actually **O(n²)** overall in a loop — each insertion at index 0 shifts all existing elements, so total work is `0 + 1 + 2 + ... + 99999`, proportional to n². `LinkedList.add(0, ...)` is `LinkedList`'s best case — direct access to `first`, true O(1) per call, no traversal needed at all.

---

## 6. Amortized O(1) — Precise Definition

Describes operations that are **usually O(1), but occasionally more expensive**, where the **average cost across a long sequence of operations** still works out to O(1) per operation.

`ArrayList.add()`: most calls are truly O(1) (place a reference in the next free slot). Occasionally — when capacity is full — one call triggers an O(n) resize-and-copy. Across `n` total `add()` calls, resizes happen roughly log₂(n) times, and the total cost of all resizes across the whole sequence is proportional to `n` (not `n²`) — so the average cost per call comes out to O(1).

**Key distinction:** amortized O(1) does NOT mean every call is fast — it means the long-run average is O(1), even though individual calls can spike. Different from strict O(1) (`ArrayList.get()`), where every single call, no exceptions, is equally fast.

---

## 7. When Both Frequent Insertion/Deletion AND Frequent `get` Are Needed

No single perfect answer — genuine trade-off analysis required:

- **If insert/delete happen mostly at the front or back** (not arbitrary middle positions): consider `ArrayDeque` — backed by a resizable array like `ArrayList`, but optimized for fast operations at both ends, without `LinkedList`'s per-node memory overhead. Often faster than `LinkedList` in practice even for queue-like use.
- **If insert/delete AND lookups both happen at arbitrary middle positions frequently:** both `ArrayList` and `LinkedList` pay an O(n) cost somewhere (shifting vs traversal) — neither is a clean win. More specialized structures (balanced trees, skip lists) could help, but usually overkill without a measured, real bottleneck.
- **In practice:** `ArrayList` is used far more often by default, even for mixed workloads, due to **cache locality** — contiguous memory is genuinely faster for the CPU to read than scattered heap nodes, due to how CPU caches work, even when Big-O alone doesn't show a difference. Big-O isn't the whole story; constant factors and hardware behavior matter too.
- **Best interview answer:** "it depends on the exact operation pattern — where insertions happen, how large the collection gets, whether there's a measured bottleneck" — shows real engineering maturity over confidently picking one option blindly.

---

## 8. Code Example

```java
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class ListComparisonDemo {
    public static void main(String[] args) {
        List<String> arrayList = new ArrayList<>();
        List<String> linkedList = new LinkedList<>();

        arrayList.add("Ashish");
        arrayList.add("Priya");
        arrayList.add(0, "Rahul"); // expensive for ArrayList — shifts everything

        linkedList.add("Ashish");
        linkedList.add("Priya");
        linkedList.add(0, "Rahul"); // cheap for LinkedList — re-points pointers

        System.out.println(arrayList);
        System.out.println(linkedList);
        System.out.println("ArrayList get(1): " + arrayList.get(1));

        // Correct interface-first usage for Deque-specific methods
        Deque<String> queue = new LinkedList<>();
        queue.addLast("First case");
        queue.addLast("Second case");
        System.out.println("Processing: " + queue.removeFirst()); // FIFO behavior
    }
}
```

### Benchmark code (measuring elapsed time correctly)
```java
public class ArrayListVsLinkedList {
    public static void main(String[] args) {
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();

        long arrayStart = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            arrayList.add(0, i);
        }
        System.out.println("ArrayList time: " + (System.currentTimeMillis() - arrayStart) + " ms");

        long linkedStart = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            linkedList.add(0, i);
        }
        System.out.println("LinkedList time: " + (System.currentTimeMillis() - linkedStart) + " ms");
    }
}
```
**Common mistake to avoid:** calling `System.currentTimeMillis()` only once, after the work is done, does NOT measure duration — it returns the current absolute timestamp, not elapsed time. You need TWO timestamps (before and after) and must subtract them to get actual elapsed duration.

---

## 9. Quick Revision Checklist

- [ ] `ArrayList` = resizable array internally; `LinkedList` = doubly linked list of `Node` objects
- [ ] Array access is O(1) via direct address arithmetic: `base + (index × elementSize)` — no searching
- [ ] `ArrayList.add()` at the end is amortized O(1) — occasional O(n) resize-and-copy when capacity is exceeded, averaged out over many calls
- [ ] `ArrayList.get(index)` = O(1); `LinkedList.get(index)` = O(n), must traverse from `first`/`last` (Java picks the closer end)
- [ ] `ArrayList` insert/remove in the middle = O(n), must shift elements; `LinkedList` splice itself is O(1) but finding the index is O(n) — so overall still O(n) unless already positioned (iterator, or `addFirst`/`addLast`)
- [ ] Both store references, not raw data — but `LinkedList` has an extra indirection layer (`Node` wrapping the reference) and higher per-element memory overhead
- [ ] `LinkedList` implements both `List` and `Deque` — use `Deque` as the declared interface type when using `addFirst`/`addLast`/etc., not the concrete `LinkedList` type
- [ ] Amortized O(1) = average cost per operation across a long sequence is O(1), even though individual operations occasionally spike — different from strict O(1) where every call is equally fast
- [ ] No universal winner for mixed insert/delete + frequent-get workloads — consider `ArrayDeque` for end-heavy operations, and remember cache locality often favors `ArrayList` in practice even when Big-O looks tied
- [ ] Measuring elapsed time requires two timestamps (before/after) and subtraction — a single `System.currentTimeMillis()` call only gives the current absolute time, not duration
