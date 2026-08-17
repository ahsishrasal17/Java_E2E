package org.ashish.learning.OOPS.Inheritance;

public class Inheritance_Demo {
    static void main() {
        SavingsAccount savingsAccount = new SavingsAccount("1234567890", 1000.0);
        System.out.println(savingsAccount);
        System.out.println("Interest: " + savingsAccount.calculateInterest());

        CurrentAccount currentAccount = new CurrentAccount("0987654321", 2000.0);
        currentAccount.deposit(2000.0);
        System.out.println(currentAccount);
        System.out.println("Interest: " + currentAccount.calculateInterest());
    }
}
