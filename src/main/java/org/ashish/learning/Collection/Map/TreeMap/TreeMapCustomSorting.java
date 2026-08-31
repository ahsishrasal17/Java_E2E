package org.ashish.learning.Collection.Map.TreeMap;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

class Account{
    private String accountId;
    private double balance;

    public Account(String accountId, double balance){
        this.accountId = accountId;
        this.balance = balance;
    }

    public String getAccountId(){
        return accountId;
    }

    public double getBalance(){
        return balance;
    }

    public String toString(){
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", balance=" + balance +
                '}';
    }
}

public class TreeMapCustomSorting {
    static void main() {
        Map<Account, String> accountByBalance = new TreeMap<>(Comparator.comparing(Account::getBalance));
        accountByBalance.put(new Account("ACC003", 3000.0), "Flagged For Review");
        accountByBalance.put(new Account("ACC001", 5000.0), "VIP Customer");
        accountByBalance.put(new Account("ACC002", 2000.0), "Regular Customer");

        System.out.println(accountByBalance);
    }
}
