package org.ashish.learning.Problem_Solving.Arrays;

public class SingleNumberOptimal {
    static void main() {
        int[] arr = {4,1,2,1,2};
        int result =singleNumber(arr);
        System.out.println(result);
    }

    static int singleNumber(int[] nums){
        int result = 0;

        for(int num : nums){
            result ^= num;
        }

        return result;
    }
}
