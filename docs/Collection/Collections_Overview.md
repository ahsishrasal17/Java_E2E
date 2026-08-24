# Collections Framework — Overview (Day 2)

## 1. What is the Collections Framework (Simple Version)

Think of it like different types of containers in a bank branch's back office:
- **`List`** = a numbered filing folder — documents go in specific slots, in order, duplicates allowed.
- **`Set`** = a stamp collection — no duplicates ever allowed; adding a duplicate is simply rejected.
- **`Map`** = a customer ID lookup card catalog — each unique key (customer ID) points to exactly one value (customer record); no scanning needed, direct lookup by key.

### Banking analogy
- `List<Transaction>` — chronological transaction history, duplicates allowed (two genuinely separate ₹500 deposits look identical but are both valid).
- `Set<String>` — blacklisted account numbers for fraud prevention; naturally prevents duplicate entries.
- `Map<String, Account>` — instant lookup of an `Account` by `accountNumber`, without scanning every account.

---

## 2. Syntax Fundamentals

```
Collection (interface)
 ├── List (interface)   — ordered, allows duplicates
 ├── Set (interface)     — no duplicates
 └── Queue (interface)   — FIFO-style processing

Map (interface)          — key-value pairs (SEPARATE hierarchy — NOT a Collection!)
```

### Critical rule
**`Map` does NOT extend `Collection`.** `List`, `Set`, `Queue` all extend `Collection`, but `Map` is a completely separate interface hierarchy — a map is "key-value pairs," not "a group of elements," which is a fundamentally different shape of data.

### Interface vs implementation
```java
List<String> names = new ArrayList<>();   // or new LinkedList<>();
Set<String> tags = new HashSet<>();       // or new TreeSet<>();
Map<String, Integer> scores = new HashMap<>(); // or new TreeMap<>();
```
Same pattern as `PaymentGateway`/`VisaGateway` from Day 1: the interface is the contract, the implementation is a specific internal strategy for fulfilling that contract.

### Rules
1. **Always declare using the interface type; reference the implementation only at construction.**
```java
List<String> names = new ArrayList<>(); // ArrayList appears ONCE — at construction
```
This means if you switch implementations later (`ArrayList` → `LinkedList`), you only change one word, at the `new` call — every other line using `names` stays untouched, because it was always written against the `List` interface. Same "swap implementations without breaking callers" payoff as Abstraction.

2. **Generics make collections type-safe containers** — `List<String>` only allows `String` elements, enforced **at compile time**:
```java
List<String> names = new ArrayList<>();
names.add("Ashish"); // compiles
names.add(123);      // compile error — int doesn't match List<String>
```
This prevents an entire category of bugs before the program ever runs, instead of surfacing them as confusing runtime errors later. (See the Type Erasure note below — this safety is compile-time only.)

3. **Collections can only hold objects, never primitives.** `List<int>` is illegal — must use the wrapper class: `List<Integer>`.

### Primitives vs wrapper classes
| Primitive | Wrapper class |
|---|---|
| `int` | `Integer` |
| `double` | `Double` |
| `boolean` | `Boolean` |
| `char` | `Character` |

A **wrapper class** is a full object version of a primitive. Collections only work with objects/references internally, so generics cannot use primitives directly — `List<int>` won't compile; `List<Integer>` will.

**Autoboxing/unboxing:** Java lets you write `list.add(5)` even though `5` is a primitive `int` — it automatically converts it to `Integer.valueOf(5)` behind the scenes (autoboxing). Retrieving it back out and assigning to an `int` variable automatically converts it back (unboxing). Convenient, but has real performance implications at scale (creating/destroying wrapper objects isn't free) — worth remembering when discussing JVM memory later.

---

## 3. Deep Dive — Type Erasure (how the JVM actually handles generics)

**The JVM does NOT know about generic types at runtime.** Type safety is enforced **only at compile time**, through a mechanism called **type erasure**.

The compiler checks every `.add()` call against the declared generic type and rejects mismatches — but once compiled, the generic type information (`<String>`, `<Integer>`) is **completely erased** from the bytecode. At runtime, `List<String>` and `List<Integer>` are literally indistinguishable — both are just a plain `List`.

**Proof:**
```java
List<String> strings = new ArrayList<>();
List<Integer> ints = new ArrayList<>();
System.out.println(strings.getClass() == ints.getClass()); // true !
```

This is why generics are a **compile-time-only safety feature** — protecting you while writing code, but with zero awareness at the JVM level once the program is actually running. This is also the root cause of the "unchecked cast" warning Java sometimes shows — situations where the compiler can't fully verify type safety and has to trust the developer.

---

## 4. JVM Perspective — Where Collections Live

Every collection object (`ArrayList`, `HashMap`, etc.) is a **regular object on the heap**, exactly like any other object (`VisaGateway`, `SavingsAccount`). There's no special JVM treatment — collections are ordinary classes from `java.util`, using the same object/reference mechanics as everything else.

```
STACK                          HEAP
+------------------+     +---------------------------+
| main() frame      |     | ArrayList object           |
| list = ref  --------->  | internal Object[] array    |
+------------------+     | [ptr, ptr, ptr, ...]  ------|---> (each ptr points to
                          | size = 3                   |      individual objects
                          +---------------------------+      elsewhere on heap)
```

An `ArrayList<String>` doesn't store `String` objects packed inside itself — it holds an array of **references**, each pointing to an actual object elsewhere on the heap. This is why adding/removing references is cheap regardless of how large the actual objects are — you're moving pointers, not copying real data.

---

## 5. Time Complexity & Space Complexity — What They Actually Mean

**Time complexity** describes how the **amount of work** an algorithm/operation does grows as input size (`n`) grows — not exact seconds/milliseconds (which depend on hardware, JVM warmup, etc.).

**Big-O notation** expresses the **worst-case growth rate**, ignoring constants and lower-order terms, because what matters for scalability is the *shape* of growth:
- **O(1)** — constant — same work regardless of `n`
- **O(n)** — linear — work grows proportionally with `n`
- **O(n²)** — quadratic — work grows with the square of `n`
- **O(log n)** — logarithmic — work grows very slowly as `n` grows

**How it's determined:** count how the number of basic operations (comparisons, assignments, pointer dereferences) scales as `n` increases, express the dominant term, drop constants.

**Space complexity** is the same idea applied to **memory** — how much additional memory a structure/algorithm needs, as a function of `n`.

---

## 6. Quick Revision Checklist

- [ ] `Collection` is the parent of `List`, `Set`, `Queue` — but `Map` is a SEPARATE hierarchy, does not extend `Collection`
- [ ] Always declare by interface type; only mention the concrete implementation at `new` — makes swapping implementations a one-line change
- [ ] Generics = compile-time type safety only; erased at runtime (type erasure) — JVM cannot distinguish `List<String>` from `List<Integer>` at runtime
- [ ] Collections require objects, not primitives — use wrapper classes (`Integer`, `Double`, etc.); autoboxing/unboxing converts automatically but isn't free performance-wise
- [ ] Collections are ordinary heap objects — no special JVM treatment; internally store references, not raw data
- [ ] Big-O measures growth rate of work/memory relative to input size `n`, not literal execution time
