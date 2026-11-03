package org.ashish.learning.Problem_Solving.Warm_Up;

public class Fibonacci {
    public static void main(String args[]) {
        int n = 10;
        fibonacci(n);
    }

    static void fibonacci(int n){
        int previous = 0;
        int current = 1;

        for(int i=0; i<n; i++) {
            System.out.print(previous + " ");
            int next = previous + current;
            previous = current;
            current = next;
        }
    }
}
