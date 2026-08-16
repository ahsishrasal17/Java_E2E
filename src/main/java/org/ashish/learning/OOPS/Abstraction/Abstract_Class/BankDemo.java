package org.ashish.learning.OOPS.Abstraction.Abstract_Class;

public class BankDemo {

    static void main() {

        SavingsAccount savings = new SavingsAccount("SAV1234", 5000);
        savings.deposit(1000);
        System.out.println("Interest earned on Savings Account: " + savings.calculateInterest());

        CurrentAccount current = new CurrentAccount("CUR1234", 10000);
        current.deposit(2000);
        System.out.println("Interest earned on Current Account: " + current.calculateInterest());
    }
}
