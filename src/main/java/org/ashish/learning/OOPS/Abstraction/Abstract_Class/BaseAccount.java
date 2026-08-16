package org.ashish.learning.OOPS.Abstraction.Abstract_Class;

public abstract class BaseAccount {

    protected final String accountNumber;
    protected double balance;

    public BaseAccount(String accountNumber, double balance){
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public void deposit(double amount){
        balance += amount;
        System.out.println("Deposited:" + amount + " " + "Current Balance is: " + balance);
    }

    public abstract double calculateInterest();
}
