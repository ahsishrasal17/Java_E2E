package org.ashish.learning.Collection.MiniProject_Bank_Transaction_Management_System;

import java.time.LocalDateTime;
import java.util.*;

public class TransactionManager {
    private Map<String, Transaction> transactionById;
    private TreeMap<LocalDateTime, List<Transaction>> transactionsByDate;
    private AccountCache<String, Account> accountCache;
    private List<Transaction> allTransactions;

    public TransactionManager(int cacheSize){
        this.transactionById = new HashMap<>();
        this.transactionsByDate = new TreeMap<>();
        this.accountCache = new AccountCache<>(cacheSize);
        this.allTransactions = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction){

        transactionById.put(transaction.getTransactionId(), transaction);

        LocalDateTime date = transaction.getTimestamp().toLocalDate().atStartOfDay();
        transactionsByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(transaction);

        allTransactions.add(transaction);
    }

    public Transaction getTransactionById(String id){
        return transactionById.get(id);
    }

    public List<Transaction> getTransactionsByDate(LocalDateTime from, LocalDateTime to){
        List<Transaction> result = new ArrayList<>();
        LocalDateTime fromDate = from.toLocalDate().atStartOfDay();
        LocalDateTime toDate = to.toLocalDate().atStartOfDay();
        for (var entry : transactionsByDate.subMap(fromDate,toDate).values())  {
            result.addAll(entry);
        }
        return result;
    }

    public List<Transaction> getAllTransactionsSorted(){
        List<Transaction> sorted = new ArrayList<>(allTransactions);
        Collections.sort(sorted);
        return sorted;
    }

    public List<Transaction> getAllTransactionsSortedByAmount(){
        List<Transaction> sorted = new ArrayList<>(allTransactions);
        sorted.sort(Comparator.comparingDouble(Transaction::getAmount));
        return sorted;
    }

    public List<Transaction> getAllTransactionsSortedByType(){
        List<Transaction> sorted = new ArrayList<>(allTransactions);
        sorted.sort(Comparator.comparing(Transaction::getType).thenComparing(Transaction::getAmount));
        return sorted;
    }

    public List<Transaction> getAccountTransactions(String accountNumber){
        List<Transaction> accountTxns = new ArrayList<>();
        for (Transaction t : allTransactions){
            if (t.getAccountNumber() == accountNumber){
                accountTxns.add(t);
            }
        }
        return accountTxns;
    }

    public Account getOrCacheAccount(String accountNumber){
        return accountCache.getOrDefault(accountNumber, null);
    }

    public void cacheAccount(String accountNumber, Account account){
        System.out.println("Caching account: " + accountNumber);
        accountCache.put(accountNumber, account);
    }

    public void printCacheStatus(){
        System.out.println("\n===Account Cache Status===");
        System.out.println("Cached accounts: " + accountCache.size());
        System.out.println("Accounts in cache: " + accountCache.keySet());
    }
}
