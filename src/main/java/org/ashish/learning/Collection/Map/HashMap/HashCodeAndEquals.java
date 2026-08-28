package org.ashish.learning.Collection.Map.HashMap;

import java.util.HashMap;
import java.util.Map;

public class HashCodeAndEquals {

    static void main() {

        Map<BankAccount,String> accountOwners = new HashMap<>();

        BankAccount acc = new BankAccount("ACC001");
        accountOwners.put(acc,"Ashish");

        BankAccount acc2 = new BankAccount("ACC001");
        System.out.println(acc.hashCode());
        System.out.println(acc2.hashCode());
        System.out.println(acc.hashCode() % 16);
        System.out.println(acc2.hashCode() % 16);
        System.out.println(accountOwners.get(acc2));

    }
}

class BankAccount{
    private String accountNumber;

    BankAccount(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
