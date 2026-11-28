package org.ashish.learning.Problem_Solving.Arrays;

import java.util.Arrays;

public class RotateArray {
    static void main() {
        int[] arr = {1,2,3,4,5,6,7};
        int k = 3;
        int[] result = rotateArray(arr, k);
        System.out.println(Arrays.toString(result));
    }

    static int[] rotateArray(int[] nums, int k){

        if(nums.length == 0){
            return nums;
        }

        k %= nums.length;

        for(int rotation=0; rotation<k; rotation++){
            int last = nums[nums.length - 1];

            for(int i= nums.length-1; i>0; i--){
                nums[i] = nums[i-1];
            }
            nums[0] = last;
        }
        return nums;
    }
}
