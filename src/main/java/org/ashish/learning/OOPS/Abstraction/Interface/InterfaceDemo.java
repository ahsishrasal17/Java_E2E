package org.ashish.learning.OOPS.Abstraction.Interface;

public class InterfaceDemo {

    static void main() {

        PaymentGateway visa = new VisaGateway();
        TransferService  visaTransfer= new TransferService(visa);
        visaTransfer.transfer(2000, "ACC001");

        PaymentGateway upi = new UpiGateway();
        TransferService upiTransfer = new TransferService(upi);
        upiTransfer.transfer(5000, "ACC002");
    }
}
