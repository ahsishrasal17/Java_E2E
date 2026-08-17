package org.ashish.learning.OOPS.Composition;

public class SubscriptionPlan {

    private String planName;
    private double monthlyFee;

    public SubscriptionPlan(String planName, double monthlyFee){
        this.planName = planName;
        this.monthlyFee = monthlyFee;
    }

    public String getPlanName(){
        return planName;
    }
}
