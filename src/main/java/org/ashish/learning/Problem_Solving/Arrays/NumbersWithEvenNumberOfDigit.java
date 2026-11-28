package org.ashish.learning.Problem_Solving.Arrays;

public class NumbersWithEvenNumberOfDigit {
    static void main(String[] args) {
        int[] arr= {12,345,2,6,7896};
        int result = numberCount(arr);
        System.out.println(result);
    }

    static int numberCount(int[] nums){
        int result = 0;
        for(int num : nums){
            int digits = 0;

            while(num>0){
                digits++;
                num /= 10;
            }

            if(digits%2 == 0){
                result++;
            }
        }
        return result;
    }
}
