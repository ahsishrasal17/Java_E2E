package org.ashish.learning.Collection.MiniProject_Bank_Transaction_Management_System;

import java.time.LocalDateTime;
import java.util.List;

public class Demo {
    static void main() {
        TransactionManager manager = new TransactionManager(3);


        // === Create test data ===
        LocalDateTime t1 = LocalDateTime.of(2024, 1, 15, 10, 30);
        LocalDateTime t2 = LocalDateTime.of(2024, 1, 16, 14, 45);
        LocalDateTime t3 = LocalDateTime.of(2024, 1, 17, 9, 15);
        LocalDateTime t4 = LocalDateTime.of(2024, 1, 18, 16, 20);

        Transaction tx1 = new Transaction("TXN001", "ACC123", 1000.0, t1, "DEBIT");
        Transaction tx2 = new Transaction("TXN002", "ACC123", 500.0, t2, "CREDIT");
        Transaction tx3 = new Transaction("TXN003", "ACC456", 2000.0, t3, "DEBIT");
        Transaction tx4 = new Transaction("TXN004", "ACC456", 300.0, t4, "TRANSFER");

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
        List<Transaction> byDate = manager.getTransactionsByDate(t1, t4);
        for (Transaction t : byDate){
            System.out.println(t);
        }


        // === Test Comparable (default sort) ===
        System.out.println("\n=== Comparable: Sorted by Date (natural order) ===");
        List<Transaction> sorted = manager.getAllTransactionsSorted();
        for (Transaction t : sorted){
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
        for(Transaction t : byType){
            System.out.println(t);
        }


        System.out.println("\n=== LRU Cache: Recently Accessed Accounts ===");
        Account acc1 = new Account("ACC001", "Ashish Rasal", 50000, LocalDateTime.now());
        Account acc2 = new Account("ACC002", "Pratik Nikam", 30000, LocalDateTime.now());
        Account acc3 = new Account("ACC003", "Darshan Chavan", 20000, LocalDateTime.now());
        Account acc4 = new Account("ACC004", "Akshay Patil", 10000, LocalDateTime.now());

        manager.cacheAccount("ACC001", acc1);
        manager.cacheAccount("ACC002", acc2);
        manager.cacheAccount("ACC003", acc3);
        manager.printCacheStatus();

        System.out.println("\nAccessing ACC001 (moves to end, recently used)");
        manager.getOrCacheAccount("ACC001");
        manager.printCacheStatus();

        System.out.println("\nAdding ACC004 (evicts ACC002, least recently used)");
        manager.cacheAccount("ACC004", acc4);
        manager.printCacheStatus();


        // === Account transactions (HashMap lookup) ===
        System.out.println("\n=== HashMap: Transactions for ACC001 ===");
        List<Transaction> acc1Txns = manager.getAccountTransactions("ACC001");
        for (Transaction t : acc1Txns){
            System.out.println(t);
        }

    }
}
