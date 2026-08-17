package org.ashish.learning.OOPS.Abstraction.Interface;

public class TransferService {
    private final PaymentGateway gateway;

    public TransferService(PaymentGateway gateway){
        this.gateway = gateway;
    }

    public void transfer(double amount, String accountNumber){
        PaymentResult result = gateway.processPayment(amount, accountNumber);
        System.out.println("Transfer Successful " + result.isSuccess());
    }
}
