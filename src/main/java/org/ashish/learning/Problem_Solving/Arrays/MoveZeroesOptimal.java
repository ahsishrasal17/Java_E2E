package org.ashish.learning.Problem_Solving.Arrays;

import java.util.Arrays;

public class MoveZeroesOptimal {
    public static void main(String args[]) {
        int[] arr = {0,1,0,3,12};
        int[] result = moveZeroes(arr);
        System.out.println(Arrays.toString(result));
    }

    static int[] moveZeroes(int[] nums){
        int write = 0;

        for(int read=0; read<nums.length; read++){
            if(nums[read] != 0){
                nums[write] = nums[read];
                write++;
            }
        }

        for(int i=write;i<nums.length;i++){
            nums[i] = 0;
        }
        return nums;
    }
}
