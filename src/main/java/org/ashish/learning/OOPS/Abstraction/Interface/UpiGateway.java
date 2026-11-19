package org.ashish.learning.OOPS.Abstraction.Interface;

public class UpiGateway implements PaymentGateway{

    @Override
    public PaymentResult processPayment(double amount, String accountNumber){
        System.out.println("Routing Rs " + amount + " via UPI....");
        return new PaymentResult(true, "UPI123456789");
    }
}
