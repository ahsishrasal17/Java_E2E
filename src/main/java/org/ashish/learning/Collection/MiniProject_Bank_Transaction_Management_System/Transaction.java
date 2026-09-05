package org.ashish.learning.Collection.MiniProject_Bank_Transaction_Management_System;

import java.time.LocalDateTime;

public class Transaction implements Comparable<Transaction>{
    private String transactionId;
    private String accountNumber;
    private double amount;
    private LocalDateTime timestamp;
    private String type;

    public Transaction(String transactionId, String accountNumber, double amount, LocalDateTime timestamp, String type) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public int compareTo(Transaction other) {
        return this.timestamp.compareTo(other.timestamp);
    }
}
