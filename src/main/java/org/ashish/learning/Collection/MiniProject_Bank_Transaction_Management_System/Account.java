package org.ashish.learning.Collection.MiniProject_Bank_Transaction_Management_System;

import java.time.LocalDateTime;

public class Account {
    private String accountNumber;
    private String accountHolder;
    private double balance;
    private LocalDateTime createdDate;

    public Account(String accountNumber, String accountHolder, double balance, LocalDateTime createdDate){
        this.accountHolder = accountHolder;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.createdDate = createdDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public String toString() {
        return "Account{" +
                ", accountHolder='" + accountHolder + '\'' +
                ", balance: ₹" + balance +
                '}';
    }

    public void updateBalance(double amount){
        if (amount <= 0){
            System.out.println("Amount should be greater than 0");
            return;
        }
        balance += amount;
    }
}
