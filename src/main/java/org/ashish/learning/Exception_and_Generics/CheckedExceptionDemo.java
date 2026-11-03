package org.ashish.learning.Exception_and_Generics;

import java.math.BigDecimal;

public class CheckedExceptionDemo {

    private static final BigDecimal FRAUD_CHECK_LIMIT = new BigDecimal("1000000");

    static class InvalidTransactionException extends Exception{
        public InvalidTransactionException(String message){
            super(message);
        }
    }

    static void validateTransaction(String accountId, BigDecimal amount) throws InvalidTransactionException{



        if (amount.compareTo(BigDecimal.ZERO) <= 0 ){
            throw new InvalidTransactionException("Amount must be positive for " + accountId + ", got" + amount);
        }

        if (amount.compareTo(FRAUD_CHECK_LIMIT) > 0) {
            throw new InvalidTransactionException("Amount exceeds limit for " + accountId + ", got" + amount);
        }
        System.out.println("Validate transaction for " + accountId + ": " + amount);
    }


    static void main() {
        try{
            validateTransaction("ACC-1001", new BigDecimal(5000));
            validateTransaction("ACC-1002", new BigDecimal(-100));
            validateTransaction("ACC-1003", new BigDecimal(2000000));
        } catch (InvalidTransactionException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}

