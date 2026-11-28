package org.ashish.learning.Problem_Solving.Arrays;

public class MaximumSubArray {
    static void main() {
        int[] arr ={-2,1,-3,4,-1,2,1,-5,4};
        int result = maximumSubArray(arr);
        System.out.println(result);
    }

    static int maximumSubArray(int[] nums){
       int currentSum = nums[0];
       int maxSum = nums[0];

       for(int i=1; i<nums.length;i++){
           currentSum = Math.max(nums[i], currentSum + nums[i]);
           maxSum = Math.max(currentSum, maxSum);
       }
       return maxSum;
    }
}
