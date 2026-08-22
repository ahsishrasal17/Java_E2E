package org.ashish.learning.OOPS.Polymorphism.CompileTime;

public class OverloadDemo {

    static void main() {
        InterestCalculator inv = new InterestCalculator();
        System.out.println(inv.calculate(1000, 0.05));
        System.out.println(inv.calculate(1000, 0.05, 5));
        System.out.println(inv.calculate(1000, 0.05));
    }
}
