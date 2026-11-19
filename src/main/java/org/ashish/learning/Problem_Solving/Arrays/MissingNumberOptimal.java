package org.ashish.learning.Problem_Solving.Arrays;

public class MissingNumberOptimal {
    static void main() {
        int[] arr = {3,0,1};
        int result = missingNumber(arr);
        System.out.println("Missing Number is: " + result);
    }

    static int missingNumber(int[] arr){
        long n = arr.length;
        long expectedSum = n * (n+1) / 2;
        long actualSum = 0;

        for(int value : arr){
            actualSum = actualSum + value;
        }
        return (int) (expectedSum - actualSum);
    }
}
