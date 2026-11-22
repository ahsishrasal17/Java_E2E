package org.ashish.learning.Problem_Solving.Arrays;

import java.util.HashMap;
import java.util.Map;

public class SingleNumber {
    public static void main(String[] args) {
        int[] arr = {1, 2, 2, 1, 4};
        int result = singleNumber(arr);
        System.out.println(result);
    }

    static int singleNumber(int[] nums){
        Map<Integer, Integer> single = new HashMap<>();

        for(int i=0; i<nums.length;i++){
            int count = single.getOrDefault(nums[i],0)+1;
            single.put(nums[i], count);
        }

        for(Map.Entry<Integer, Integer> entry : single.entrySet()){
            if(entry.getValue() == 1){
                return entry.getKey();
            }
        }
        return 0;
    }
}
