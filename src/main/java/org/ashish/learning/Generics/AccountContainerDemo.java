package org.ashish.learning.Generics;

public class AccountContainerDemo {
    static class AccountContainer<T> {
        private T item;

        public void store(T item){
            this.item = item;
        }

        public T retrieve(){
            return item;
        }
    }

    record Account(int id, double balance){}
    record Transaction(int id, String type, double amount){}

    static void main() {
        AccountContainer<Account> accBox = new AccountContainer<>();
        accBox.store(new Account(100, 5000));
        Account acc = accBox.retrieve();

        AccountContainer<Transaction> trns = new AccountContainer<>();
        trns.store(new Transaction(01, "DEPOSIT", 2000));
        System.out.println(acc);
        System.out.println(trns.retrieve());

        //accBox.store(new Transaction(02, "CREDIT", 3000)); - This will give error
    }

}
