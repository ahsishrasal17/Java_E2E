package org.ashish.learning.OOPS.Abstraction.Interface;

public class VisaGateway implements PaymentGateway{
    private final String apiKey = "visa-secret-key";

    @Override
    public PaymentResult processPayment(double amount, String accountNumber){
        System.out.println("Rouring Rs" + amount + " via Visa Network....");
        return new PaymentResult(true, "VISA123456789");
    }
}
