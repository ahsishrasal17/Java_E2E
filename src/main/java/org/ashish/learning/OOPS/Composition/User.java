package org.ashish.learning.OOPS.Composition;

public class User {

    private String userName;
    private SubscriptionPlan plan;

    public User(String userName, SubscriptionPlan plan){
        this.userName = userName;
        this.plan = plan;
    }

    public void updatePlan(SubscriptionPlan newplan){
        this.plan = newplan;
        System.out.println("User " + userName + " has updated the plan to " + plan.getPlanName());
    }

    public String getPlanName(){
        return plan.getPlanName();
    }
}
