package org.ashish.learning.OOPS.MiniProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseAccount {
    private final String accountNumber;
    private double balance;
    private List<String> transactionHistory = new ArrayList<>();
    private NotificationService notificationService;

    BaseAccount(String accountNumber, double balance, NotificationService notificationService) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.notificationService = notificationService;
    }

    public void deposit(double amount){
        if (amount <= 0){
            notificationService.sendAlert(accountNumber, "Deposit failed: Amount cannot be negative.");
        }else{
            balance += amount;
            notificationService.sendAlert(accountNumber, "Deposited: " + amount);
            transactionHistory.add("Deposited: " + amount);
        }
    }

    public void withdraw(double amount){
        if (amount <= 0){
            notificationService.sendAlert(accountNumber, "Withdrawal failed: Amount must be positive.");
        }else if(amount > balance){
            notificationService.sendAlert(accountNumber, "Withdrawal failed: Insufficient funds.");
        }else{
            balance -= amount;
            notificationService.sendAlert(accountNumber, "Withdrew: " + amount);
            transactionHistory.add("Withdrew: " + amount);
        }
    }

    public double getBalance(){
        return balance;
    }

    public List<String> getTransactionHistory(){
        return Collections.unmodifiableList(transactionHistory);
    }

    public abstract double calculateInterest();

    @Override
    public String toString() {
        return getClass().getSimpleName()  + "{" +
                "accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", transactionHistory=" + transactionHistory +
                ", notificationService=" + notificationService.getClass().getSimpleName() +
                '}';
    }
}
