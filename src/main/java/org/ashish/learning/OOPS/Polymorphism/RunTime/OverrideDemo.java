package org.ashish.learning.OOPS.Polymorphism.RunTime;

import java.util.Collections;

public class OverrideDemo {

    static void main() {
        BaseAccount[] accounts = {
                new SavingsAccount(5000),
                new CurrentAccount(8000),
        };

        for(BaseAccount account : accounts){
            System.out.println(account.getClass().getSimpleName() + " Interest: " + account.calculateInterest());
        }
    }
}
