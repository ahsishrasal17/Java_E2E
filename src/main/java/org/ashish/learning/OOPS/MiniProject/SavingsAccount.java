package org.ashish.learning.OOPS.MiniProject;

public class SavingsAccount extends BaseAccount{

    SavingsAccount(String accountNumber, double balance, NotificationService notificationService){
        super(accountNumber, balance, notificationService);
    }

    public double calculateInterest(){
        return getBalance() * 0.04;
    }
}
