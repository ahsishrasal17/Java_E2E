package org.ashish.learning.Problem_Solving.Warm_Up;

public class PrimeNumberOptimal {
    public static void main(String args[]) {
        int n = 11;
        boolean result = isPrime(n);
        if(result){
            System.out.println(n + " is a prime number");
        } else {
            System.out.println(n + " is not a prime number");
        }
    }

    static boolean isPrime(int n){
        if(n<2){
            return false;
        }
        for (int i=2; i*i<=n; i++){
            if (n%i == 0){
                return false;
            }
        }
        return true;
    }

}

