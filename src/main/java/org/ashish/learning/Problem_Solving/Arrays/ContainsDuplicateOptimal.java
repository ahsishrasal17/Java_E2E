package org.ashish.learning.Problem_Solving.Arrays;

import java.util.HashMap;
import java.util.Map;

public class ContainsDuplicateOptimal {
    public static void main(String args[]) {
        int[] arr = {1, 2, 3, 1,};
        boolean result = containsDuplicate(arr);
        System.out.println(result);
    }

    static boolean containsDuplicate(int[] nums) {
        Map<Integer, Integer> entry = new HashMap<>();

        for(int value : nums){
            int count = entry.getOrDefault(value, 0) + 1;
            if(count > 1){
                return true;
            }
            entry.put(value,count);
        }
        return false;
    }
}
