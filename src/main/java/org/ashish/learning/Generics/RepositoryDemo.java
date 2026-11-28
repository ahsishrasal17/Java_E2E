package org.ashish.learning.Generics;

import java.util.*;

public class RepositoryDemo {
    static class Repository<T> {
        private final Map<Integer, T> store = new HashMap<>();

        public void store(int id, T item){
            store.put(id,item);
        }

        public Optional<T> findByID(int id){
            return Optional.ofNullable(store.get(id));
        }

        public List<T> findAll(){
            return new ArrayList<>(store.values());
        }
    }

    record Account(int id, String holder){}
    record Transaction(int id, double amount){}

    static void main() {
        Repository<Account> accounts = new Repository<>();
        accounts.store(1, new Account(1, "Ashish"));
        accounts.store(2, new Account(2, "Pratik"));

        Repository<Transaction> txns = new Repository<>();
        txns.store(10, new Transaction(10, 2500.0));

        //accounts.store(11, new Transaction(11, 3000.0)); - This gives error

        System.out.println(accounts.findByID(1).orElseThrow());
        System.out.println(accounts.findAll().size() + " Accounts");
        System.out.println(txns.findAll());
    }
}
