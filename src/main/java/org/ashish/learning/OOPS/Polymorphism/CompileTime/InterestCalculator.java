package org.ashish.learning.OOPS.Polymorphism.CompileTime;

public class InterestCalculator {

    public double calculate(double principal, double rate){
        return principal * rate;
    }

    public double calculate(double principal, double rate, int years){
        return  principal * rate * years;
    }

    public double calculate(int principal, double rate){
        return principal * rate;
    }
}
