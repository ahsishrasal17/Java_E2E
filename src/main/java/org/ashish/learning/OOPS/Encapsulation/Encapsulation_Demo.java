package org.ashish.learning.OOPS.Encapsulation;


import java.util.ArrayList;
import java.util.List;

class Account{

    private String accountNumber;
    private double balance;
    private List<String> transactionHistory = new ArrayList<>();

    public Account(String accountNumber, double balance){
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public double getBalance(){
        return balance;
    }

    public void deposit(double amount){
        if (amount <= 0){
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance += amount;
        transactionHistory.add("Deposited: " + amount);
    }

    public void withdraw(double amount){
        if (amount <= 0){
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > balance){
            throw new IllegalStateException("Insufficient Funds");
        }
        balance -= amount;
        transactionHistory.add("Withdrew: " + amount);
    }

    public List<String> getTransactionHistory(){
        return new ArrayList<>(transactionHistory);
    }
}


public class Encapsulation_Demo {
    static void main() {
        Account account = new Account("ACC01234", 5000);
        account.deposit(1000);
        System.out.println("Current Balance: " + account.getBalance());

        account.withdraw(2000);
        System.out.println("Current Balance after withdrawal: " + account.getBalance());
        System.out.println(account.getTransactionHistory());

        List<String> hacked = account.getTransactionHistory();
        hacked.add("Hcaked - FAKE Transaction");

        System.out.println(account.getTransactionHistory());
    }
}
