package org.ashish.learning.Problem_Solving.Arrays;

import java.util.Arrays;

public class NumbersSmallerThanCurrentNumber {
    static void main() {
        int[] arr = {8,1,2,2,3};
        int[] result = smallerCount(arr);
        System.out.println(Arrays.toString(result));
    }

    static int[] smallerCount(int[] nums){
        int[] result = new int[nums.length];
        int count = 0;
        for(int i=0; i<nums.length;i++){
            int numberCount = 0;
            for(int j=0; j< nums.length;j++){
                if(nums[j] < nums[i]){
                    numberCount++;
                }
            }
            result[count] = numberCount;
            count++;
        }
        return result;
    }
}
