package org.ashish.learning.Problem_Solving.Warm_Up;

public class SwapTwoNumbersXOR {
    public static void main(String args[]) {
        int a = 3;
        int b = 9;

        a = a ^ b;
        b = a ^ b;
        a = a ^ b;

        System.out.println("a = " + a + ", b = " + b);
    }
}
