package org.ashish.learning.Problem_Solving.Warm_Up;

public class PrimeNumber {
    public static void main(String args[]) {
        int n = 10;
        boolean result = isPrime(n);
        if(result){
            System.out.println(n + " is a prime number");
        }else{
            System.out.println(n + " is not a prime number");
        }
    }
        static boolean isPrime(int n){

        if(n<2){
            return false;
        }

        for(int i=2; i<=n; i++){
            if(n%i == 0 && i != n){
                return  false;
            }
        }

        return true;
    }
}
