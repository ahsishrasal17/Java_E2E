package org.ashish.learning.Exception_and_Generics;

import java.math.BigDecimal;

public class TryCatchFinally {

    static class InsufficientBalanceException extends Exception{
        public InsufficientBalanceException(String message){
            super(message);
        }
    }

    static BigDecimal withdraw(String accountId, BigDecimal balance, BigDecimal amount) throws InsufficientBalanceException{
        if (amount.compareTo(balance) > 0){
            throw new InsufficientBalanceException("Balance" + balance + "is less then requested " + amount);
        }
        return balance.subtract(amount);
    }

    static void attemptWithdrawl(String accountId, BigDecimal balance, BigDecimal amount) {
        System.out.println("Attempt: " + accountId + " withdraw " + amount);
        try {
            BigDecimal newBalance = withdraw(accountId, balance, amount);
            System.out.println("Withdraw successful. New balance: " + newBalance);
            return;
        }catch(InsufficientBalanceException e){
            System.out.println("Withdraw failed: " + e.getMessage());
        }finally {
            System.out.println("AUDIT LOG: withdrawal attempt recorded for " + accountId);
        }
        System.out.println("Reached after try-catch-finally (only on failure path)");
    }

    static void main() {
        attemptWithdrawl("ACC-1001", new BigDecimal("1000"), new BigDecimal("400"));
        attemptWithdrawl("ACC-1001", new BigDecimal("1000"), new BigDecimal("1200"));
    }
}
