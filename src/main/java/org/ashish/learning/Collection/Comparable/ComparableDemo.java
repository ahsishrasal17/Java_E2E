package org.ashish.learning.Collection.Comparable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Account implements Comparable<Account>{

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

    @Override
    public int compareTo(Account other){
        return this.accountId.compareTo(other.accountId);
    }

}

public class ComparableDemo {

    static void main() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("ACC003", 45000.0));
        accounts.add(new Account("ACC001", 15000.0));
        accounts.add(new Account("ACC002", 25000.0));

        Collections.sort(accounts);

        for(Account account: accounts){
            System.out.println(account.getAccountId() + " - " + account.getBalance());
        }
    }
}
