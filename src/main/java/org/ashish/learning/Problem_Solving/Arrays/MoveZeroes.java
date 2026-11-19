package org.ashish.learning.Problem_Solving.Arrays;

import java.util.Arrays;

public class MoveZeroes {
    static void main() {
        int[] arr = {0,1,0,3,12};
        int[] result = moveZeroes(arr);
        System.out.println(Arrays.toString(result));
    }

    static int[] moveZeroes(int[] nums){
        int[] edited = new int[nums.length];
        int count = 0;

        for(int i=0; i< nums.length; i++){
            if(nums[i] != 0){
                edited[count] = nums[i];
                count++;
            }
        }
        System.arraycopy(edited,0,nums,0,nums.length);
        return nums;
    }
}
