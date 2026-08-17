package org.ashish.learning.OOPS.Inheritance;

public abstract class BaseAccount {

    protected String accountNumber;
    protected double balance;

    public BaseAccount(String accountNumber, double balance){
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public void deposit(double amount){
        balance += amount;
    }

    public String toString(){
        return getClass().getSimpleName() + "{accountNumber='" + accountNumber + "', balance=" + balance + "}";
    }

    public abstract double calculateInterest();
}
