package org.ashish.learning.OOPS.Inheritance;

public class CurrentAccount extends BaseAccount{

        public CurrentAccount(String accountNumber, double balance){
            super(accountNumber, balance);
        }

        public double calculateInterest(){
            return 0;
        }
}
