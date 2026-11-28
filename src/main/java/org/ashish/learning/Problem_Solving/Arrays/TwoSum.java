package org.ashish.learning.Problem_Solving.Arrays;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TwoSum {
    static void main() {
        int[] arr = {2,11,7,15};
        int target = 9;
        int[] result = twoSum(arr, target);
        System.out.println(Arrays.toString(result));
    }

    static int[] twoSum(int[] nums, int target){
        Map<Integer, Integer> entries = new HashMap<>();

        for(int i=0; i<nums.length; i++){
            int requiredValue = target - nums[i];

           if(entries.containsKey(requiredValue)){
               return new int[] {entries.get(requiredValue), i};
           }
           entries.put(nums[i], i);
        }
        return new int[0];
    }
}
