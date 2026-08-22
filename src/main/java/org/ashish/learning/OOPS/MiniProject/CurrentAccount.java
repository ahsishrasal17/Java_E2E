package org.ashish.learning.OOPS.MiniProject;

public class CurrentAccount extends BaseAccount{

    public CurrentAccount(String accountNumber, double balance, NotificationService notificationService){
        super(accountNumber, balance, notificationService);
    }

    public double calculateInterest(){
        return 0;
    }
}
