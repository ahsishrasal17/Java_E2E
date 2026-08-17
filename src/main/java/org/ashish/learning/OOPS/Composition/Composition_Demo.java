package org.ashish.learning.OOPS.Composition;

public class Composition_Demo {

    public static void main(String[] args) {
        SubscriptionPlan basicPlan = new SubscriptionPlan("Basic", 9.99);
        User user = new User("Ashish", basicPlan);

        user.updatePlan(new SubscriptionPlan("Premium", 19.99));
        System.out.println(user.getPlanName());
    }
}
