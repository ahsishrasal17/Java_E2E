# HashMap & Two Sum Optimal Solution (Day 2)

## 1. What is HashMap (Simple Version)

Think of a **library with a smart shelving system**. Instead of one giant shelf where you'd scan every book to find "Harry Potter," the library has **numbered sections** — a rule: *"take the book's title, run it through a formula, and the formula tells you exactly which section number to put it in."*

When you want to **find** "Harry Potter" later, run the **same formula** on the title again — it gives you the same section number instantly. You only search **within that one small section**, not the whole library.

- **Formula** = hashing
- **Section number** = bucket
- **Instead of O(n) search**, jump almost directly to the right group: O(1)

### Banking analogy
Looking up an `Account` by `accountNumber` in a `Map<String, Account>` — instead of scanning every account linearly (like a `List`), the account number gets **hashed to a bucket**, and `HashMap` goes almost directly there. This is why `HashMap` lookups are **O(1)** — dramatically faster than a `List`'s O(n) linear search.

---

## 2. Syntax Fundamentals

```java
Map<String, Integer> ages = new HashMap<>();

ages.put("Ashish", 28);       // insert/update key-value pair
ages.get("Ashish");            // retrieve value by key → 28
ages.containsKey("Ashish");    // check if key exists → true
ages.remove("Ashish");         // remove by key
ages.getOrDefault("Priya", 0); // return 0 if "Priya" isn't a key
```

### Rules
1. **Keys must be unique.** Calling `put()` with an existing key **overwrites** the old value — doesn't add a second entry.
2. **`HashMap` is NOT thread-safe.** Multiple threads modifying it concurrently can corrupt its structure.
3. **Both keys and values can be `null`** — but only **one** `null` key allowed (uniqueness rule).
4. **Iteration order is NOT guaranteed** — unlike `LinkedHashMap` or `TreeMap`.

---

## 3. Internal Structure: Array of Linked Lists (Node[])

### The Foundation
`HashMap` is built on **both an array AND linked lists**:
- An **array of buckets** (`Node[] table`) — initially 16 slots
- Each bucket can hold a **chain of Node objects** (linked list) if collisions occur

**Memory picture:**
```
HashMap<String, Integer>
└─ Node[] table (array of 16 buckets)
   [0]: null
   [1]: Node ──→ Node ──→ Node   (chain of 3 entries)
   [2]: Node                     (single entry)
   [3]: null
   ... (more buckets)
   
Each Node contains:
├─ key (String object reference)
├─ value (Integer object reference)
└─ next (reference to next Node, or null if no collision)
```

**Critical point:** the `table` array doesn't store String and Integer objects directly — it stores **references to Node objects**, each of which holds references to the actual String and Integer objects elsewhere on the heap.

### How collisions form chains
When two different keys hash to the **same bucket index**, they form a **linked list chain** inside that bucket:
```
put("Ashish", 28):   hash("Ashish") % 16 = 5  → bucket[5]
put("Rohan", 31):    hash("Rohan") % 16 = 5   → bucket[5] (collision!)

Result:
bucket[5] ──→ Node("Ashish", 28) ──→ Node("Rohan", 31) ──→ null
```

---

## 4. How Hashing Works

### Step 1: Calculate hashCode()
Every object in Java has a `hashCode()` method (inherited from `Object` or overridden). It returns an `int` — a numeric "fingerprint" of the object.

```java
String key = "Ashish";
int hash = key.hashCode();  // e.g., 1970302568
```

### Step 2: Compute bucket index
`HashMap` converts the hash code to a bucket index using modulo:
```java
int bucketIndex = Math.abs(hash) % tableSize;
// Math.abs() needed because hash codes can be negative
// Example: 1970302568 % 16 = 8
```

**Why `Math.abs()`?** Hash codes can be negative — directly using `% 16` on a negative number returns a negative bucket index, which is invalid. HashMap internally handles this with bit operations, but the concept is: ensure the result is always `0` to `tableSize-1`.

### Complete example
```java
String key1 = "Ashish";
String key2 = "Praktik";

int hash1 = key1.hashCode();  // 1970302568
int hash2 = key2.hashCode();  // -1896349257

int bucket1 = Math.abs(hash1) % 16;  // 8
int bucket2 = Math.abs(hash2) % 16;  // 9

// "Ashish" goes to bucket 8, "Praktik" goes to bucket 9
```

### Why hash codes can be negative
Hash codes are computed from object content and can legitimately be negative. This is just how math works:
```java
// From your test:
int hash = "Praktik".hashCode();  // -1896349257
int bucket = hash % 16;           // -9 (invalid!)
int bucketFixed = Math.abs(hash) % 16;  // 9 (valid)
```

---

## 5. Lookup Process (get)

When you call `map.get("Ashish")`:

1. **Hash the key:**
   ```
   hash("Ashish") → 1970302568
   bucket index → 8
   ```

2. **Go to bucket 8** and walk the chain:
   ```
   bucket[8] ──→ Node1 ──→ Node2 ──→ Node3 ──→ null
   ```

3. **For each node in the chain, call `equals()`:**
   ```
   Is Node1.key.equals("Ashish")? → Yes! Return Node1.value
   (or No? → Check Node2, then Node3, etc.)
   ```

**This is why BOTH `hashCode()` and `equals()` matter:**
- `hashCode()` gets you to the right bucket (fast — O(1))
- `equals()` confirms you found the right key (walks the chain — O(chain length))

If hash function is good and collisions are few:
- Average chain length ≈ 1
- Total time ≈ O(1)

If hash function is bad and many collisions:
- Chain length could be O(n)
- **Java 8 fix:** treeify chains of 8+ entries into Red-Black Trees (O(log n))

---

## 6. The `hashCode()`/`equals()` Contract (CRITICAL)

### The Rule
> **If two objects are `.equals()`, they MUST have the same `.hashCode()`**

Breaking this contract corrupts `HashMap` (and `HashSet`):

```java
class BankAccount {
    private String accountNumber;
    
    BankAccount(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    // Problem: overriding equals() WITHOUT overriding hashCode()
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BankAccount)) return false;
        return accountNumber.equals(((BankAccount)obj).accountNumber);
    }
    
    // NOT overriding hashCode() — uses Object's default (memory identity)
}

Map<BankAccount, String> map = new HashMap<>();

BankAccount acc1 = new BankAccount("ACC001");
BankAccount acc2 = new BankAccount("ACC001");

map.put(acc1, "Ashish");
String owner = map.get(acc2);  // null — WRONG!

// Why?
// acc1.equals(acc2) → true (same account number)
// But:
// acc1.hashCode() → based on memory address (e.g., 12345678)
// acc2.hashCode() → based on memory address (e.g., 87654321)
// Different hash codes → different buckets → HashMap never finds acc2!
```

### The Fix
Override **BOTH** or **NEITHER**:

```java
@Override
public boolean equals(Object obj) {
    if (!(obj instanceof BankAccount)) return false;
    return accountNumber.equals(((BankAccount)obj).accountNumber);
}

@Override
public int hashCode() {
    return accountNumber.hashCode();  // Now equal objects have equal hash codes
}

// Now:
// acc1.equals(acc2) → true
// acc1.hashCode() == acc2.hashCode() → true
// Same bucket → HashMap finds acc2!
```

### Overriding only `equals()` (not `hashCode()`)
Result: Duplicate key entries in the same bucket.

### Overriding only `hashCode()` (not `equals()`)
Result: Same hash code, but `equals()` returns false, so treated as different keys in same bucket.

**Always override BOTH together.**

---

## 7. Collisions & Chaining Strategy

### What is a collision?
Two different keys hash to the **same bucket index**:
```java
hash("Ashish") % 16 = 5
hash("Rohan") % 16 = 5    // Collision!
```

### Handling via chaining
`HashMap` stores **both** in the same bucket as a linked list:
```
bucket[5]:
  Node("Ashish", 28) ──→ Node("Rohan", 31) ──→ null
```

When you `get("Rohan")`:
1. Hash → bucket 5
2. Walk chain: check Node1.equals("Rohan")? No.
3. Move to Node2: check Node2.equals("Rohan")? Yes! Return 31.

### Bad hash functions and attacks
A **bad hash function** returns the same value for many different keys:
```java
class PoorHashKey {
    @Override
    public int hashCode() {
        return 42;  // TERRIBLE! Every key hashes to 42
    }
}
```

With this, 1000 different keys all go to the same bucket → chain of 1000 entries → `get()` must walk 1000 nodes → **O(1000) = O(n)**!

**Attack scenario:** malicious actor deliberately submits input designed to collide:
```
Put 1000 requests with keys that all hash to the same bucket
→ That bucket has a 1000-node chain
→ Each lookup walks 1000 nodes
→ Server becomes slow (Denial of Service)
```

### Java 8's defense: Treeification
If a single bucket's chain grows to **8+ entries** (threshold) **AND** total HashMap capacity is ≥ **64** buckets, Java automatically converts that chain to a **Red-Black Tree**:
```
Before: bucket[5] ──→ Node ──→ Node ──→ ... ──→ Node (8+ chain)
After:  bucket[5] ──→ RedBlackTree (O(log n) lookup instead of O(n))
```

A Red-Black Tree is a self-balancing binary search tree. With 8 entries, a tree is only ~3 levels deep (log₂(8) ≈ 3), so worst-case lookup is O(3) instead of O(8).

**Important:** treeification only happens when both conditions are true:
- Single bucket has 8+ entries
- HashMap capacity is at least 64

This prevents creating trees for tiny HashMaps — not worth the overhead.

---

## 8. Load Factor and Resizing

`HashMap` doesn't let buckets get too crowded. It uses a **load factor** to decide when to grow:

### Load factor formula
```java
load_factor = size / capacity
// Default: 0.75
// Threshold: capacity × 0.75
```

### Example
```java
HashMap<String, Integer> map = new HashMap<>();
// Initial capacity: 16
// Load factor: 0.75
// Threshold: 16 × 0.75 = 12

// After putting 12 entries: size = 12, threshold = 12
// Putting the 13th entry: size becomes 13
// 13 > 12? YES → RESIZE!

// New capacity: 32 (doubled)
// New threshold: 32 × 0.75 = 24
// All 13 entries are RE-HASHED into the new 32-bucket array
```

### Why resize instead of just allowing collisions?
Collisions degrade performance:
```
1000 entries, 16 buckets:
  Average per bucket: 1000/16 ≈ 62 entries
  get() must walk ~62 nodes → O(62) ≈ O(n)

1000 entries, 256 buckets (after multiple resizes):
  Average per bucket: 1000/256 ≈ 4 entries
  get() walks ~4 nodes → O(4) ≈ O(1)
```

Load factor (0.75) is a tuning knob — grow before too many collisions accumulate, keeping average bucket size small and maintaining O(1) lookups.

---

## 9. Identity vs Content-Based Hash Codes

### Object's default (identity-based)
```java
// No override — uses Object's hashCode()
class Person {
    String name;
    Person(String name) { this.name = name; }
}

Person p1 = new Person("Ashish");
Person p2 = new Person("Ashish");

System.out.println(System.identityHashCode(p1));  // e.g., 12345678
System.out.println(System.identityHashCode(p2));  // e.g., 87654321
System.out.println(p1 == p2);                      // false
System.out.println(p1.equals(p2));                 // false
```

Different objects in memory → different identity hash codes → different buckets in HashMap.

### String's override (content-based)
```java
String s1 = new String("Ashish");
String s2 = new String("Ashish");

System.out.println(s1.hashCode());     // Same — based on string content
System.out.println(s2.hashCode());     // Same
System.out.println(s1 == s2);          // false — different objects
System.out.println(s1.equals(s2));     // true — same content
```

String overrides both `hashCode()` and `equals()` to compare **content**, not identity. Two different String objects with identical text are treated as the same key:
```java
Map<String, Integer> ages = new HashMap<>();
ages.put("Ashish", 28);
ages.put(new String("Ashish"), 29);  // Different object, same content
System.out.println(ages.size());     // 1 — overwrote, not added
System.out.println(ages.get("Ashish")); // 29
```

### Identity hash code
```java
System.identityHashCode(obj);  // Always based on memory address
                                // Different for different objects, always
System.out.println(System.identityHashCode(s1));  // e.g., 1234
System.out.println(System.identityHashCode(s2));  // e.g., 5678
// Even if s1 and s2 have identical content!
```

---

## 10. JVM Perspective — Memory Layout

```
HashMap object (heap)
+------------------------+
| table -----------------------> Object[] buckets, each slot either:
| size = 2                |         - null (empty)
| capacity = 16           |         - a Node (single entry)
| loadFactor = 0.75       |         - a chain of Nodes (linked list, collision)
| threshold = 12          |         - a small Red-Black Tree (treeified, 8+ collisions)
+------------------------+

Bucket array (heap):
[0]: null
[1]: Node ──→ Node ──→ null
[2]: Node ──→ null
[3]: null
...
[15]: null

Each Node (heap object):
+------------------+
| key: "Ashish"    |────→ actual String object (elsewhere on heap)
| value: 28        |────→ actual Integer wrapper object (elsewhere on heap)
| next: ──────────→ next Node in chain (or null)
+------------------+
```

**Key insight:** both the bucket array AND every Node are separate heap objects. References chain together to form the bucket structure.

---

## 11. Time Complexity Summary

| Operation | Average Case | Worst Case |
|---|---|---|
| `get(key)` | O(1) | O(n) — poor hash function |
| `put(key, value)` | O(1) | O(n) — poor hash function + resize |
| `remove(key)` | O(1) | O(n) — poor hash function |
| `containsKey(key)` | O(1) | O(n) — poor hash function |

**Assumptions for O(1):**
- Hash function distributes keys evenly across buckets
- Collisions are rare and chains stay short

**Java 8+ guarantee:**
- Even with bad hash function or attack, worst case capped at O(log n) due to treeification

---

---

# TWO SUM OPTIMAL SOLUTION

## 12. The Problem

Given an array of integers `nums` and an integer `target`, return the **indices** of the two numbers that add up to `target`. You may assume each input has exactly one solution, and you cannot use the same element twice.

**Example:**
```
Input: nums = [2, 7, 11, 15], target = 9
Output: [0, 1]  (because nums[0] + nums[1] = 2 + 7 = 9)
```

---

## 13. Brute Force Approach (O(n²))

```java
public int[] twoSum(int[] nums, int target) {
    for (int i = 0; i < nums.length; i++) {
        for (int j = i + 1; j < nums.length; j++) {
            if (nums[i] + nums[j] == target) {
                return new int[]{i, j};
            }
        }
    }
    return new int[]{};
}
```

**Time complexity:** O(n²) — for every element, scan the rest of the array  
**Space complexity:** O(1) — only constant extra space

**Why it's slow:** for an array of 1000 elements, you do roughly 500,000 comparisons in the worst case.

---

## 14. Optimal Approach — HashMap Strategy (O(n))

### The Key Insight

Instead of scanning **forward** looking for a partner, **look backward** using what you've already seen.

As you iterate through each element `nums[i]`, ask yourself:
> *"What number would I need to have seen already to make a pair that sums to target?"*

**Answer:** `complement = target - nums[i]`

If you store every number you've seen in a HashMap (as you iterate), you can instantly check: *"have I seen this complement before?"* — O(1) lookup.

### Step-by-step example

```
nums = [2, 7, 11, 15], target = 9

i=0, nums[0]=2:
  complement = 9 - 2 = 7
  Have I seen 7? No.
  Remember: seen = {2→0}

i=1, nums[1]=7:
  complement = 9 - 7 = 2
  Have I seen 2? YES! (at index 0)
  Return [0, 1]
```

---

## 15. The Code

```java
public int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> seen = new HashMap<>();
    // Key: number we've seen
    // Value: index where we saw it
    
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        
        // Have we seen the complement before?
        if (seen.containsKey(complement)) {
            return new int[]{seen.get(complement), i};
        }
        
        // Not found yet; remember this number for future lookups
        seen.put(nums[i], i);
    }
    
    return new int[]{};  // No solution found
}
```

**Time complexity:** O(n) — single pass through array, each HashMap operation is O(1)  
**Space complexity:** O(n) — HashMap stores up to n elements

---

## 16. Common Doubts & Clarifications

### Doubt 1: Does this only work for sorted or positive arrays?

**Answer: NO — it works for ANY array, sorted or unsorted, positive or negative or mixed.**

The algorithm doesn't depend on order or sign at all:

```java
// Unsorted array
int[] nums1 = {11, 2, 15, 7};  // Unsorted
int target1 = 9;
// Still finds [1, 3] (nums[1]=2, nums[3]=7, sum=9)

// Negative numbers
int[] nums2 = {-2, 7, 11, -5};
int target2 = 5;
// Still finds [-2, 7] correctly

// All negative
int[] nums3 = {-5, -2, -3, -8};
int target3 = -10;
// Still works fine

// Mixed
int[] nums4 = {-5, 10, 3, -2};
int target4 = 8;
// Still finds [1, 3] (nums[1]=10, nums[3]=-2, sum=8)
```

HashMap doesn't care about sorting or sign — it's a pure **lookup-by-value** strategy.

### Doubt 2: What if the complement is negative?

**Answer: That's NOT a problem — HashMap handles negative keys perfectly.**

```java
nums = [-5, 10], target = 20

i=0, nums[0]=-5:  complement = 20 - (-5) = 25 (positive)
                  seen = {-5→0}

i=1, nums[1]=10:  complement = 20 - 10 = 10
                  seen = {-5→0}
```

And if the complement itself is negative:

```java
nums = [5, -10], target = -5

i=0, nums[0]=5:   complement = -5 - 5 = -10 (negative)
                  seen = {5→0}

i=1, nums[1]=-10: complement = -5 - (-10) = 5
                  seen = {5→0, -10→1}
                  Found! 5 is in seen.
```

HashMap hashes and stores negative integers exactly like positive ones — no special handling needed.

### Doubt 3: Return the actual numbers instead of indices

This is a legitimate variation:

```java
public int[] twoSumReturnValues(int[] nums, int target) {
    Map<Integer, Integer> seen = new HashMap<>();
    
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        
        if (seen.containsKey(complement)) {
            // Instead of returning indices, return the actual values
            return new int[]{complement, nums[i]};
        }
        
        seen.put(nums[i], i);
    }
    
    return new int[]{};
}

// Test:
int[] nums = {2, 7, 11, 15};
int target = 9;
System.out.println(Arrays.toString(twoSumReturnValues(nums, target)));
// Output: [2, 7] — the actual numbers, not indices
```

---

## 17. Understanding `Arrays.toString()` — Why we need it

### The Problem: Raw Array Print

```java
int[] nums = {2, 7, 11};

// Direct print (without toString):
System.out.println(nums);  // Output: [I@1f32e575
```

This garbage (`[I@1f32e575`) is the default `Object.toString()`:
- `[I` = "array of int" (type descriptor)
- `@` = separator
- `1f32e575` = memory address (hashcode)

Completely useless for actually seeing the array's contents!

### The Solution: `Arrays.toString()`

```java
import java.util.Arrays;

int[] nums = {2, 7, 11};
System.out.println(Arrays.toString(nums));  // Output: [2, 7, 11]
```

Much better — readable, formatted array representation.

### How `Arrays.toString()` actually works

```java
// Roughly what Java's Arrays.toString() does internally:
public static String toString(int[] arr) {
    if (arr == null) return "null";
    
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < arr.length; i++) {
        sb.append(arr[i]);                     // Add the number
        if (i < arr.length - 1) {
            sb.append(", ");                   // Add comma separator
        }
    }
    sb.append("]");                            // Close bracket
    return sb.toString();
}
```

**Breaking this down:**
1. Handle null case
2. Create a `StringBuilder` to efficiently build a string
3. Add opening bracket `[`
4. Loop through each element:
   - Append the element's value
   - If not the last element, append `, ` (comma + space)
5. Add closing bracket `]`
6. Return the final string

### Manual implementation (without `Arrays.toString()`)

If you want to understand exactly how this works without using the utility:

```java
int[] nums = {2, 7, 11};

// Manual approach:
StringBuilder result = new StringBuilder("[");
for (int i = 0; i < nums.length; i++) {
    result.append(nums[i]);
    if (i < nums.length - 1) {
        result.append(", ");
    }
}
result.append("]");
System.out.println(result.toString());  // [2, 7, 11]
```

**Result is identical to `Arrays.toString()`**, but now you understand the mechanism.

---

## 18. Complete Code Example with All Variations

```java
package org.ashish.learning.Collection.Map;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TwoSum {

    /**
     * Optimal solution: returns indices of two numbers that sum to target
     * Time: O(n), Space: O(n)
     */
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            
            if (seen.containsKey(complement)) {
                return new int[]{seen.get(complement), i};
            }
            seen.put(nums[i], i);
        }
        
        return new int[]{};
    }

    /**
     * Variant: returns the actual numbers instead of indices
     */
    public int[] twoSumReturnValues(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            
            if (seen.containsKey(complement)) {
                return new int[]{complement, nums[i]};
            }
            seen.put(nums[i], i);
        }
        
        return new int[]{};
    }

    /**
     * Manual implementation of Arrays.toString()
     * Shows exactly how the utility method works internally
     */
    public static String manualArrayToString(int[] arr) {
        if (arr == null) return "null";
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}

class TwoSumDemo {
    public static void main(String[] args) {
        TwoSum solution = new TwoSum();
        
        System.out.println("=== Test Case 1: Basic (positive, unsorted) ===");
        int[] nums1 = {2, 7, 11, 15};
        int target1 = 9;
        int[] result1 = solution.twoSum(nums1, target1);
        System.out.println("Array: " + Arrays.toString(nums1));
        System.out.println("Target: " + target1);
        System.out.println("Result (indices): " + Arrays.toString(result1));
        
        System.out.println("\n=== Test Case 2: Unsorted ===");
        int[] nums2 = {3, 2, 4};
        int target2 = 6;
        int[] result2 = solution.twoSum(nums2, target2);
        System.out.println("Array: " + Arrays.toString(nums2));
        System.out.println("Target: " + target2);
        System.out.println("Result (indices): " + Arrays.toString(result2));
        
        System.out.println("\n=== Test Case 3: Duplicates ===");
        int[] nums3 = {3, 3};
        int target3 = 6;
        int[] result3 = solution.twoSum(nums3, target3);
        System.out.println("Array: " + Arrays.toString(nums3));
        System.out.println("Target: " + target3);
        System.out.println("Result (indices): " + Arrays.toString(result3));
        
        System.out.println("\n=== Test Case 4: Negative numbers ===");
        int[] nums4 = {-5, 10, 3, -2};
        int target4 = 8;
        int[] result4 = solution.twoSum(nums4, target4);
        System.out.println("Array: " + Arrays.toString(nums4));
        System.out.println("Target: " + target4);
        System.out.println("Result (indices): " + Arrays.toString(result4));
        System.out.println("Verification: nums[" + result4[0] + "] + nums[" + result4[1] + "] = " 
            + nums4[result4[0]] + " + " + nums4[result4[1]] + " = " + (nums4[result4[0]] + nums4[result4[1]]));
        
        System.out.println("\n=== Test Case 5: All negative ===");
        int[] nums5 = {-5, -2, -3, -8};
        int target5 = -10;
        int[] result5 = solution.twoSum(nums5, target5);
        System.out.println("Array: " + Arrays.toString(nums5));
        System.out.println("Target: " + target5);
        System.out.println("Result (indices): " + Arrays.toString(result5));
        
        System.out.println("\n=== Variant: Return values instead of indices ===");
        int[] valueResult = solution.twoSumReturnValues(nums1, target1);
        System.out.println("Array: " + Arrays.toString(nums1));
        System.out.println("Target: " + target1);
        System.out.println("Result (values): " + Arrays.toString(valueResult));
        
        System.out.println("\n=== Understanding Arrays.toString() ===");
        int[] demo = {1, 2, 3};
        System.out.println("Raw array print: " + demo);  // Ugly hash
        System.out.println("Arrays.toString(): " + Arrays.toString(demo));  // Nice
        System.out.println("Manual toString(): " + TwoSum.manualArrayToString(demo));  // Same result
    }
}
```

### Expected Output:

```
=== Test Case 1: Basic (positive, unsorted) ===
Array: [2, 7, 11, 15]
Target: 9
Result (indices): [0, 1]

=== Test Case 2: Unsorted ===
Array: [3, 2, 4]
Target: 6
Result (indices): [1, 2]

=== Test Case 3: Duplicates ===
Array: [3, 3]
Target: 6
Result (indices): [0, 1]

=== Test Case 4: Negative numbers ===
Array: [-5, 10, 3, -2]
Target: 8
Result (indices): [1, 3]
Verification: nums[1] + nums[3] = 10 + -2 = 8

=== Test Case 5: All negative ===
Array: [-5, -2, -3, -8]
Target: -10
Result (indices): [0, 2]

=== Variant: Return values instead of indices ===
Array: [2, 7, 11, 15]
Target: 9
Result (values): [2, 7]

=== Understanding Arrays.toString() ===
Raw array print: [I@1f32e575
Arrays.toString(): [1, 2, 3]
Manual toString(): [1, 2, 3]
```

---

## 19. Key Takeaways

✅ HashMap is backed by an array of linked lists — not array or pure linked list  
✅ Hash code → modulo → bucket index; `equals()` confirms exact key match in chain  
✅ `hashCode()`/`equals()` contract: override BOTH or NEITHER  
✅ Load factor (0.75) triggers resize before collisions accumulate  
✅ Treeification (Java 8+) caps worst-case at O(log n)  
✅ Two Sum works with ANY array — sorted/unsorted, positive/negative/mixed  
✅ HashMap strategy: look backward using what you've seen, not forward scanning  
✅ `Arrays.toString()` is just a utility that iterates and formats — now you understand it  

---

## 20. Quick Revision Checklist

### HashMap Internals
- [ ] HashMap is array of buckets + linked lists (Node chains)
- [ ] Hash code calculation: `bucketIndex = hashCode() % capacity`
- [ ] Lookup: hash to bucket, then walk chain using `equals()`
- [ ] **`hashCode()`/`equals()` contract:** if equal, same hash — override BOTH
- [ ] Load factor (0.75) triggers resize; new capacity = 2× old
- [ ] Treeification at 8+ chain length (if capacity ≥ 64) → O(log n)
- [ ] Identity vs content hash: Object defaults use memory address; String uses content
- [ ] Negative hash codes handled with `Math.abs()` or internal spreading function
- [ ] Bad hash functions or attacks cause collisions; treeification is Java's defense

### Two Sum Optimal
- [ ] O(n) via HashMap vs O(n²) brute force
- [ ] Works with ANY array: sorted/unsorted, positive/negative/mixed
- [ ] Negative complements and negative keys cause no problems
- [ ] Can return indices or actual values (just change what's returned)
- [ ] `Arrays.toString()` internally builds formatted string via StringBuilder
- [ ] Raw array print produces memory address garbage without toString()
