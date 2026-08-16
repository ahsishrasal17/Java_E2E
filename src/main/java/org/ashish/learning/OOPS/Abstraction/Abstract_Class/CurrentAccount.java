package org.ashish.learning.OOPS.Abstraction.Abstract_Class;

public class CurrentAccount extends BaseAccount{

    public CurrentAccount(String accoutNumber, double balance){
        super(accoutNumber, balance);
    }

    public double calculateInterest(){
        return 0;
    }
}
