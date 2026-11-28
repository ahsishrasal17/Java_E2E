package org.ashish.learning.Problem_Solving.Arrays;

import java.util.Arrays;

public class RotateArrayOptimal {
    static void main() {
        int[] arr = {1,2,3,4,5,6,7};
        int k = 3;
        int[] result = rotateArray(arr, k);
        System.out.println(Arrays.toString(result));
    }

    static int[] rotateArray(int[] nums, int k){

        int n = nums.length;
        k %= n;

        reverse(nums, 0, n -1);
        reverse(nums, 0, k-1);
        reverse(nums, k, n-1);
        return nums;
    }

    static void reverse(int[] nums, int left, int right){
        while(left < right) {
            int temp = nums[left];
            nums[left] = nums[right];
            nums[right] = temp;
            left++;
            right--;
        }
    }
}
