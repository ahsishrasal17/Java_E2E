package org.ashish.learning.Problem_Solving.Arrays;

import java.util.HashMap;
import java.util.Map;

public class MajorityElement {
    static void main() {
        int[] arr = {2,2,1,1,1,2,2};
        int result = majorityElement(arr);
        System.out.println(result);
    }

    static int majorityElement(int[] nums){
       int n = nums.length/2;

        Map<Integer, Integer> entries = new HashMap<>();

        for(int i=0; i<nums.length; i++){
            int count = entries.getOrDefault(nums[i],0)+1;
            entries.put(nums[i],count);
        }

            for(Map.Entry<Integer, Integer> entry : entries.entrySet()){
                if(entry.getValue() > n){
                    return entry.getKey();
                }
        }
            return 0;
    }
}
