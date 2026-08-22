package org.ashish.learning.OOPS.MiniProject;

public class EmailNotification implements NotificationService{

    public void sendAlert(String accountNumber, String message){
        System.out.println("Email sent to account number: " + accountNumber + " with message: " + message);
    }
}
