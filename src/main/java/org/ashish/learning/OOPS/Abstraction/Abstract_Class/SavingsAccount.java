package org.ashish.learning.OOPS.Abstraction.Abstract_Class;

public class SavingsAccount extends BaseAccount{

    public SavingsAccount(String accountNumber, double balance){
        super(accountNumber, balance);
    }

    public double calculateInterest(){
        return balance * 0.04;
    }
}
