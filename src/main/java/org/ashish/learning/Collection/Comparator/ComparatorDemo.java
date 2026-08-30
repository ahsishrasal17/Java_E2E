package org.ashish.learning.Collection.Comparator;


import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Account implements Comparator<Account>{

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

    /*@Override
    public int compareTo(Account other){
        return this.accountId.compareTo(other.accountId);
    }*/

    public static Comparator<Account> BY_BALANCE = new Comparator<Account>() {

        @Override
        public int compare(Account a1, Account a2){
            return Double.compare(a1.getBalance(), a2.getBalance());
        }
    };

    @Override
   public int compare(Account o1, Account o2) {
        return 0;
    }
}


public class ComparatorDemo {
    static void main() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("ACC03", 45000.0));
        accounts.add(new Account("ACC002", 20000.0));
        accounts.add(new Account("ACC001", 75000.0));

        Collections.sort(accounts, Account.BY_BALANCE);
        for(Account account : accounts){
            System.out.println(account.getAccountId() + " - " + account.getBalance());
        }
    }
}
