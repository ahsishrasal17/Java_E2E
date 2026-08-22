package org.ashish.learning.OOPS.MiniProject;

public class SmsNotification implements NotificationService{

    public void sendAlert(String accountNumber, String message){
        System.out.println("SMS sent to account number: " + accountNumber + " with message: " + message);
    }
}
