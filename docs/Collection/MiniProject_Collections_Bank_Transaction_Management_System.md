# Day 2 Mini-Project: Bank Transaction Management System

## Overview

Build a **complete transaction management system** that integrates ALL Day 2 Collections concepts:
- HashMap (fast lookups by transaction ID)
- TreeMap (transactions sorted by date)
- LinkedHashMap (transaction history in order)
- Comparable/Comparator (sorting by amount, date, type)
- LRU Cache (recently accessed accounts cache)

This is a **real-world scenario**: a bank needs to track millions of transactions efficiently, sorted in multiple ways, with fast lookups and intelligent caching.

**Time estimate:** 2-3 hours  
**Deliverables:**
1. Working Java code with all Collections concepts
2. `.md` file documenting the system
3. GitHub commit with full project

---

## Project Requirements

### 1. Core Data Classes

#### `Transaction.java`
```java
public class Transaction implements Comparable<Transaction> {
    private String transactionId;     // Unique ID (HashMap key)
    private String accountNumber;     // Account involved
    private double amount;            // Transaction amount
    private LocalDateTime timestamp;  // When it happened
    private String type;              // DEBIT, CREDIT, TRANSFER
    
    // Constructor, getters
    
    // Comparable: sort by timestamp (default)
    @Override
    public int compareTo(Transaction other) {
        return this.timestamp.compareTo(other.timestamp);
    }
}
```

#### `Account.java`
```java
public class Account {
    private String accountNumber;
    private String accountHolder;
    private double balance;
    private LocalDateTime createdDate;
    // Constructor, getters
}
```

### 2. Collections Usage (MUST demonstrate all concepts)

#### HashMap — Fast Lookup
```java
Map<String, Transaction> transactionById = new HashMap<>();
// Purpose: retrieve any transaction by ID instantly
// O(1) lookup
```

#### TreeMap — Sorted by Date
```java
Map<LocalDateTime, List<Transaction>> transactionsByDate = new TreeMap<>();
// Purpose: iterate transactions in chronological order
// O(log n) but guaranteed sorted
```

#### LinkedHashMap — Insertion Order
```java
Map<String, Account> recentlyAccessedAccounts = new LRUCache<>(100);
// Purpose: cache recently looked-up accounts (LRU eviction)
// O(1) with ordering guarantee
```

#### Comparable/Comparator — Multiple Sorts
```java
List<Transaction> allTransactions = new ArrayList<>();

// Sort by date (natural order via Comparable)
Collections.sort(allTransactions);

// Sort by amount (custom order via Comparator)
Collections.sort(allTransactions, (t1, t2) -> Double.compare(t1.getAmount(), t2.getAmount()));

// Sort by type, then amount (chained Comparators)
Comparator<Transaction> comp = Comparator.comparing(Transaction::getType)
                                         .thenComparingDouble(Transaction::getAmount);
Collections.sort(allTransactions, comp);
```

#### LRU Cache — Custom Implementation
```java
// Extend LinkedHashMap with access-order mode + size limit
class AccountCache<K, V> extends LinkedHashMap<K, V> {
    private int maxSize;
    
    public AccountCache(int maxSize) {
        super(16, 0.75f, true);  // true = access-order
        this.maxSize = maxSize;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > maxSize;  // evict when size exceeded
    }
}
```

---

## Project Architecture

```
TransactionManager
├─ HashMap<String, Transaction> transactionById
│  └─ for: get transaction by ID instantly
│
├─ TreeMap<LocalDateTime, List<Transaction>> transactionsByDate
│  └─ for: iterate transactions in chronological order
│
├─ LinkedHashMap<String, Account> accountCache (LRU)
│  └─ for: cache recently accessed accounts (evict oldest unused)
│
├─ Comparable<Transaction>
│  └─ for: default sorting by date
│
├─ Comparator<Transaction> (multiple)
│  └─ for: custom sorting by amount, type, account
│
└─ Methods:
    ├─ addTransaction(Transaction)
    ├─ getTransactionById(String id)
    ├─ getTransactionsByDateRange(LocalDateTime from, LocalDateTime to)
    ├─ getAllTransactionsSorted() [by date]
    ├─ getAllTransactionsSortedByAmount()
    ├─ getAllTransactionsSortedByType()
    ├─ getAccountTransactions(String accountNumber)
    ├─ getOrCacheAccount(String accountNumber) [uses LRU Cache]
    └─ printAccountCacheStats()
```

---

## Step-by-Step Implementation

### Step 1: Create `Transaction.java`

```java
package org.ashish.learning.Collection.MiniProject;

import java.time.LocalDateTime;

public class Transaction implements Comparable<Transaction> {
    private String transactionId;
    private String accountNumber;
    private double amount;
    private LocalDateTime timestamp;
    private String type; // DEBIT, CREDIT, TRANSFER
    
    public Transaction(String transactionId, String accountNumber, double amount, 
                      LocalDateTime timestamp, String type) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.timestamp = timestamp;
        this.type = type;
    }
    
    // Comparable: sort by timestamp (natural order)
    @Override
    public int compareTo(Transaction other) {
        return this.timestamp.compareTo(other.timestamp);
    }
    
    // Getters
    public String getTransactionId() { return transactionId; }
    public String getAccountNumber() { return accountNumber; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getType() { return type; }
    
    @Override
    public String toString() {
        return type + " | " + amount + " | " + accountNumber + " | " + timestamp;
    }
}
```

### Step 2: Create `Account.java`

```java
package org.ashish.learning.Collection.MiniProject;

import java.time.LocalDateTime;

public class Account {
    private String accountNumber;
    private String accountHolder;
    private double balance;
    private LocalDateTime createdDate;
    
    public Account(String accountNumber, String accountHolder, double balance, 
                  LocalDateTime createdDate) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.createdDate = createdDate;
    }
    
    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public double getBalance() { return balance; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    
    public void updateBalance(double amount) { this.balance += amount; }
    
    @Override
    public String toString() {
        return accountHolder + " (" + accountNumber + ") | Balance: ₹" + balance;
    }
}
```

### Step 3: Create `AccountCache.java` (LRU Implementation)

```java
package org.ashish.learning.Collection.MiniProject;

import java.util.LinkedHashMap;
import java.util.Map;

public class AccountCache<K, V> extends LinkedHashMap<K, V> {
    private int maxSize;
    
    public AccountCache(int maxSize) {
        super(16, 0.75f, true); // true = access-order mode (LRU)
        this.maxSize = maxSize;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (size() > maxSize) {
            System.out.println("LRU: Evicting " + eldest.getKey());
            return true;
        }
        return false;
    }
}
```

### Step 4: Create `TransactionManager.java` (Main Logic)

```java
package org.ashish.learning.Collection.MiniProject;

import java.time.LocalDateTime;
import java.util.*;

public class TransactionManager {
    private Map<String, Transaction> transactionById;              // HashMap
    private Map<LocalDateTime, List<Transaction>> transactionsByDate; // TreeMap
    private AccountCache<String, Account> accountCache;            // LRU Cache
    private List<Transaction> allTransactions;
    
    public TransactionManager(int cacheSize) {
        this.transactionById = new HashMap<>();
        this.transactionsByDate = new TreeMap<>();
        this.accountCache = new AccountCache<>(cacheSize);
        this.allTransactions = new ArrayList<>();
    }
    
    // 1. Add transaction (HashMap + TreeMap + List)
    public void addTransaction(Transaction transaction) {
        transactionById.put(transaction.getTransactionId(), transaction);
        
        LocalDateTime date = transaction.getTimestamp().toLocalDate().atStartOfDay();
        transactionsByDate.computeIfAbsent(date, k -> new ArrayList<>())
                         .add(transaction);
        
        allTransactions.add(transaction);
    }
    
    // 2. Get by ID (HashMap O(1))
    public Transaction getTransactionById(String id) {
        return transactionById.get(id);
    }
    
    // 3. Get by date range (TreeMap sorted access)
    public List<Transaction> getTransactionsByDateRange(LocalDateTime from, LocalDateTime to) {
        List<Transaction> result = new ArrayList<>();
        LocalDateTime fromDate = from.toLocalDate().atStartOfDay();
        LocalDateTime toDate = to.toLocalDate().atStartOfDay();
        
        for (var entry : transactionsByDate.subMap(fromDate, toDate).values()) {
            result.addAll(entry);
        }
        return result;
    }
    
    // 4. Get all sorted by date (Comparable)
    public List<Transaction> getAllTransactionsSorted() {
        List<Transaction> sorted = new ArrayList<>(allTransactions);
        Collections.sort(sorted); // uses Comparable.compareTo()
        return sorted;
    }
    
    // 5. Get sorted by amount (Comparator)
    public List<Transaction> getAllTransactionsSortedByAmount() {
        List<Transaction> sorted = new ArrayList<>(allTransactions);
        sorted.sort(Comparator.comparingDouble(Transaction::getAmount));
        return sorted;
    }
    
    // 6. Get sorted by type, then amount (chained Comparator)
    public List<Transaction> getAllTransactionsSortedByType() {
        List<Transaction> sorted = new ArrayList<>(allTransactions);
        sorted.sort(Comparator.comparing(Transaction::getType)
                             .thenComparingDouble(Transaction::getAmount));
        return sorted;
    }
    
    // 7. Get account transactions (HashMap for ID lookup)
    public List<Transaction> getAccountTransactions(String accountNumber) {
        List<Transaction> accountTxns = new ArrayList<>();
        for (Transaction t : allTransactions) {
            if (t.getAccountNumber().equals(accountNumber)) {
                accountTxns.add(t);
            }
        }
        return accountTxns;
    }
    
    // 8. Get or cache account (LRU Cache)
    public Account getOrCacheAccount(String accountNumber) {
        return accountCache.getOrDefault(accountNumber, null);
    }
    
    public void cacheAccount(String accountNumber, Account account) {
        System.out.println("Cache: Adding " + accountNumber);
        accountCache.put(accountNumber, account);
    }
    
    // 9. Cache stats
    public void printCacheStats() {
        System.out.println("\n=== Account Cache Stats ===");
        System.out.println("Cached accounts: " + accountCache.size());
        System.out.println("Accounts in cache: " + accountCache.keySet());
    }
}
```

### Step 5: Create `Demo.java` (Test Everything)

```java
package org.ashish.learning.Collection.MiniProject;

import java.time.LocalDateTime;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        TransactionManager manager = new TransactionManager(3); // Cache size = 3
        
        // === Create test data ===
        LocalDateTime t1 = LocalDateTime.of(2024, 1, 15, 10, 30);
        LocalDateTime t2 = LocalDateTime.of(2024, 1, 16, 14, 45);
        LocalDateTime t3 = LocalDateTime.of(2024, 1, 17, 9, 15);
        LocalDateTime t4 = LocalDateTime.of(2024, 1, 18, 16, 20);
        
        Transaction tx1 = new Transaction("TXN001", "ACC001", 5000, t1, "CREDIT");
        Transaction tx2 = new Transaction("TXN002", "ACC002", 2000, t2, "DEBIT");
        Transaction tx3 = new Transaction("TXN003", "ACC001", 10000, t3, "TRANSFER");
        Transaction tx4 = new Transaction("TXN004", "ACC003", 1500, t4, "CREDIT");
        
        manager.addTransaction(tx1);
        manager.addTransaction(tx2);
        manager.addTransaction(tx3);
        manager.addTransaction(tx4);
        
        // === Test HashMap (get by ID) ===
        System.out.println("=== HashMap: Get by ID ===");
        System.out.println(manager.getTransactionById("TXN001"));
        System.out.println(manager.getTransactionById("TXN003"));
        
        // === Test TreeMap (sorted by date) ===
        System.out.println("\n=== TreeMap: Transactions by Date Range ===");
        List<Transaction> byDate = manager.getTransactionsByDateRange(t1, t4);
        for (Transaction t : byDate) {
            System.out.println(t);
        }
        
        // === Test Comparable (default sort) ===
        System.out.println("\n=== Comparable: Sorted by Date (natural order) ===");
        List<Transaction> sorted = manager.getAllTransactionsSorted();
        for (Transaction t : sorted) {
            System.out.println(t);
        }
        
        // === Test Comparator (custom sort by amount) ===
        System.out.println("\n=== Comparator: Sorted by Amount ===");
        List<Transaction> byAmount = manager.getAllTransactionsSortedByAmount();
        for (Transaction t : byAmount) {
            System.out.println(t);
        }
        
        // === Test chained Comparator (type, then amount) ===
        System.out.println("\n=== Chained Comparator: By Type, then Amount ===");
        List<Transaction> byType = manager.getAllTransactionsSortedByType();
        for (Transaction t : byType) {
            System.out.println(t);
        }
        
        // === Test LRU Cache ===
        System.out.println("\n=== LRU Cache: Recently Accessed Accounts ===");
        Account acc1 = new Account("ACC001", "Ashish Kumar", 50000, LocalDateTime.now());
        Account acc2 = new Account("ACC002", "Priya Singh", 75000, LocalDateTime.now());
        Account acc3 = new Account("ACC003", "Rohan Patel", 60000, LocalDateTime.now());
        Account acc4 = new Account("ACC004", "Neha Sharma", 100000, LocalDateTime.now());
        
        manager.cacheAccount("ACC001", acc1);
        manager.cacheAccount("ACC002", acc2);
        manager.cacheAccount("ACC003", acc3);
        manager.printCacheStats();
        
        System.out.println("\nAccessing ACC001 (moves to end, recently used)");
        manager.getOrCacheAccount("ACC001");
        manager.printCacheStats();
        
        System.out.println("\nAdding ACC004 (evicts ACC002, least recently used)");
        manager.cacheAccount("ACC004", acc4);
        manager.printCacheStats();
        
        // === Account transactions (HashMap lookup) ===
        System.out.println("\n=== HashMap: Transactions for ACC001 ===");
        List<Transaction> acc1Txns = manager.getAccountTransactions("ACC001");
        for (Transaction t : acc1Txns) {
            System.out.println(t);
        }
    }
}
```

---

## Expected Output

```
=== HashMap: Get by ID ===
CREDIT | 5000.0 | ACC001 | 2024-01-15T10:30
TRANSFER | 10000.0 | ACC001 | 2024-01-17T09:15

=== TreeMap: Transactions by Date Range ===
CREDIT | 5000.0 | ACC001 | 2024-01-15T10:30
DEBIT | 2000.0 | ACC002 | 2024-01-16T14:45
TRANSFER | 10000.0 | ACC001 | 2024-01-17T09:15

=== Comparable: Sorted by Date (natural order) ===
CREDIT | 5000.0 | ACC001 | 2024-01-15T10:30
DEBIT | 2000.0 | ACC002 | 2024-01-16T14:45
TRANSFER | 10000.0 | ACC001 | 2024-01-17T09:15
CREDIT | 1500.0 | ACC003 | 2024-01-18T16:20

=== Comparator: Sorted by Amount ===
CREDIT | 1500.0 | ACC003 | 2024-01-18T16:20
DEBIT | 2000.0 | ACC002 | 2024-01-16T14:45
CREDIT | 5000.0 | ACC001 | 2024-01-15T10:30
TRANSFER | 10000.0 | ACC001 | 2024-01-17T09:15

=== Chained Comparator: By Type, then Amount ===
CREDIT | 1500.0 | ACC003 | 2024-01-18T16:20
CREDIT | 5000.0 | ACC001 | 2024-01-15T10:30
DEBIT | 2000.0 | ACC002 | 2024-01-16T14:45
TRANSFER | 10000.0 | ACC001 | 2024-01-17T09:15

=== LRU Cache: Recently Accessed Accounts ===
Cache: Adding ACC001
Cache: Adding ACC002
Cache: Adding ACC003
=== Account Cache Stats ===
Cached accounts: 3
Accounts in cache: [ACC001, ACC002, ACC003]

Accessing ACC001 (moves to end, recently used)
=== Account Cache Stats ===
Cached accounts: 3
Accounts in cache: [ACC002, ACC003, ACC001]

Adding ACC004 (evicts ACC002, least recently used)
LRU: Evicting ACC002
Cache: Adding ACC004
=== Account Cache Stats ===
Cached accounts: 3
Accounts in cache: [ACC003, ACC001, ACC004]

=== HashMap: Transactions for ACC001 ===
CREDIT | 5000.0 | ACC001 | 2024-01-15T10:30
TRANSFER | 10000.0 | ACC001 | 2024-01-17T09:15
```

---

## What This Project Demonstrates

✅ **HashMap** — O(1) lookup by transaction ID  
✅ **TreeMap** — automatic sorting by date, range queries  
✅ **LinkedHashMap** — LRU cache with access-order eviction  
✅ **Comparable** — natural ordering (by date)  
✅ **Comparator** — custom sorting (by amount, by type, chained)  
✅ **Collections algorithms** — sort(), subMap(), computeIfAbsent()  
✅ **Real-world scenario** — banking transaction management  
✅ **Interview-ready** — demonstrates deep Collections knowledge  

---

## Deliverables Checklist

- [ ] `Transaction.java` — implements Comparable, complete code
- [ ] `Account.java` — data class for accounts
- [ ] `AccountCache.java` — LRU implementation extending LinkedHashMap
- [ ] `TransactionManager.java` — main logic using all Collections concepts
- [ ] `Demo.java` — test all 8 features, shows output
- [ ] Code compiles and runs without errors
- [ ] Create `.md` file: `15_Day2_MiniProject_Collections.md`
- [ ] Commit to GitHub: `git commit -m "Day 2 Complete: Bank Transaction Management System — HashMap, TreeMap, LinkedHashMap, Comparable/Comparator, LRU Cache"`

---

## Next Steps

1. **Code the 4 Java files** (copy-paste the code above into your IDE)
2. **Test locally** — run Demo.java, confirm output matches
3. **Create `.md` file** — document the system, how each Collection is used, what you learned
4. **Commit to GitHub** — final Day 2 commit
5. **Start Day 3** — Exceptions, Generics, then Concurrency, Streams, JVM internals

---

**This project is your capstone for Collections.** It shows you understand not just individual data structures, but how to combine them for real-world problems.

Build it, test it, document it, commit it.

Then Day 2 is **COMPLETE**. ✅
