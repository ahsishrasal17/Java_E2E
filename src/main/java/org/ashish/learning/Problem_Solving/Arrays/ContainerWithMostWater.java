package org.ashish.learning.Problem_Solving.Arrays;

public class ContainerWithMostWater {
    public static void main(String[] args) {
        int[] arr = {1,8,6,2,5,4,8,3,7};
        int result = mostWater(arr);
        System.out.println(result);
    }

    static int mostWater(int[] nums){
        int left = 0;
        int right = nums.length - 1;
        int finalResult = 0;

        while(left < right){
            int breadth = right - left;
            int min = Math.min(nums[left], nums[right]);
            int result = breadth * min;
            if(result > finalResult){
                finalResult = result;
            }
            if(nums[left] < nums[right]){
                left++;
            }else{
                right--;
            }
        }
        return finalResult;
    }
}
