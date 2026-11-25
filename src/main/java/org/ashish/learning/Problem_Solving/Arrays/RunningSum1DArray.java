package org.ashish.learning.Problem_Solving.Arrays;

import java.util.Arrays;

public class RunningSum1DArray {
    static void main() {
        int[] arr = {1,2,3,4};
        int[] result = runningSum(arr);
        System.out.println(Arrays.toString(result));
    }

    static int[] runningSum(int[] nums){
        int sum = 0;
        int[] runningSum = new int[nums.length];
        int count = 0;
        for(int i=0; i<nums.length;i++){
            sum = sum + nums[i];
            runningSum[count] = sum;
            count++;
        }
        return runningSum;
    }
}
