package org.ashish.learning.Collection.Map.LinkedHashMap;

import java.util.LinkedHashMap;
import java.util.Map;

class Account{

     private final String accountId;
     private double balance;

     public Account(String accountId, double balance){
         this.accountId = accountId;
         this.balance = balance;
     }

     public String getAccountId(){
         return accountId;
     }

     public String toString(){
        return "Account ID: " + accountId + ", Balance: " + balance;
     }
}

public class AccountCache extends LinkedHashMap<String, Account> {

   private final int capacity;

   AccountCache(int capacity, float loadFactor, boolean accessOrder){
       super(capacity, loadFactor, accessOrder);
       this.capacity = capacity;
   }

   @Override
   protected boolean removeEldestEntry(Map.Entry<String, Account> eldest) {
       return size() > capacity;
   }

    static void main(String[] args) {
        AccountCache cache = new AccountCache(5, 0.75f, true);

        cache.put("ACC001", new Account("ACC001", 1000.0));
        cache.put("ACC002", new Account("ACC002", 2000.0));
        cache.put("ACC003", new Account("ACC003", 3000.0));
        cache.put("ACC004", new Account("ACC004", 4000.0));
        cache.put("ACC005", new Account("ACC005", 5000.0));

        System.out.println("Before ACC006 insert" + cache.keySet());
        cache.get("ACC001");
        System.out.println("After ACC001 access" + cache.keySet());
        cache.put("ACC006", new Account("ACC006", 6000.0));
        System.out.println("After ACC006 insert" + cache.keySet());
    }

}


