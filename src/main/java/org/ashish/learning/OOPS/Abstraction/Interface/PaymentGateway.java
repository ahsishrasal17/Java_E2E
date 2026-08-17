package org.ashish.learning.OOPS.Abstraction.Interface;

public interface PaymentGateway {
    PaymentResult processPayment(double amount, String accountNumber);
}
