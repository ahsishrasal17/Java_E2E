package org.ashish.learning.OOPS.Polymorphism.RunTime;

public abstract class BaseAccount {
    protected double balance;

    public BaseAccount(double balance){
        this.balance = balance;
    }

    public abstract double calculateInterest();
}

class SavingsAccount extends BaseAccount {

    public SavingsAccount(double balance){
        super(balance);
    }

    public double calculateInterest(){
        return balance * 0.04;
    }
}

class CurrentAccount extends BaseAccount {

    public CurrentAccount(double balance){
        super(balance);
    }

    public double calculateInterest(){
        return 0;
    }
}