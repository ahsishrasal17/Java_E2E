package org.ashish.learning.Problem_Solving.Warm_Up;

public class Factorial {
    public static void main(String args[]) {
        int num = 5;
        int result = factorial(num);
        System.out.println("Factorial of " + num + " is: " + result);
    }

    static int factorial(int n){
        int i = 1;
        int result = 1;
        while (i<=n){
            result = result * i;
            i++;
        }
        return result;
    }
}
