package org.ashish.learning.OOPS.MiniProject;

public class OOPSDemo {
    static void main() {
        BaseAccount savingsAccount = new SavingsAccount("SA123", 1000, new EmailNotification());
        BaseAccount currentAccount = new CurrentAccount("CA123", 2000, new SmsNotification());

        savingsAccount.deposit(10000);
        currentAccount.deposit(5000);

        savingsAccount.withdraw(5000);
        currentAccount.withdraw(2000);

        System.out.println("Savings Account's Balance is: " + savingsAccount.getBalance());
        System.out.println("Current Account's Balance is: " +currentAccount.getBalance());

        System.out.println("Intereset for Savings Account is: " + savingsAccount.calculateInterest());
        System.out.println("Intereset for Current Account is: " + currentAccount.calculateInterest());

        System.out.println(savingsAccount.getTransactionHistory());
        System.out.println(currentAccount.getTransactionHistory());

        System.out.println(savingsAccount);
        System.out.println(currentAccount);
    }
}
