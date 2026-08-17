package org.ashish.learning.OOPS.Encapsulation;


class CardDetails{
    private String cardNumber;
    private String cvv;
    private double balance;

    public CardDetails(String cardNumber, String cvv, double balance){
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.balance = balance;
    }

    public String getMaskedCardNumber(){
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    public boolean validatePayment(String cvv){
        return this.cvv.equals(cvv);
    }

    public void transferMoney(double amount, String cvv){
        if (amount <= 0){
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (validatePayment(cvv)) {
            balance = balance - amount;
            System.out.println("Transfer Successful. Remaining Balance: " + balance);
        }else{
            System.out.println("CVV is incorrect");
        }
    }
}


public class Encapsulation_demo1 {
    public static void main(String[] args) {

        CardDetails cardDetails = new CardDetails("1234-5678-9876-5432", "123", 5000);
        System.out.println(cardDetails.getMaskedCardNumber());
        cardDetails.transferMoney(2000, "123");
    }
}
