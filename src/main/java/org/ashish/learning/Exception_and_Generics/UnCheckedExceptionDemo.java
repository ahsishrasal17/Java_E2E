package org.ashish.learning.Exception_and_Generics;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class UnCheckedExceptionDemo {

    static class AccountNotFoundException extends RuntimeException{
        public AccountNotFoundException(String accountId){
            String str = "Account not found: " + accountId;
            super(str);
        }
    }


    static BigDecimal getBalance(Map<String, BigDecimal> accounts, String accountId){
        BigDecimal balance = accounts.get(accountId);
        if (balance == null){
            throw new AccountNotFoundException(accountId);
        }
        return balance;
    }

    static void main() {
        Map<String, BigDecimal> accounts = new HashMap<>();
        accounts.put("ACC-1001", new BigDecimal("50000"));

        System.out.println("Balance: " + getBalance(accounts, "ACC-1001"));

        try{
            System.out.println("Balance: " + getBalance(accounts, "ACC-1002"));
        } catch (AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
