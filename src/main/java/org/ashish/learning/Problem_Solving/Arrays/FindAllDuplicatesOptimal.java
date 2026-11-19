package org.ashish.learning.Problem_Solving.Arrays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindAllDuplicatesOptimal {
    static void main() {
        int[] arr = {4, 3, 2, 7, 8, 2, 3, 1};
        List<Integer> result = findDuplicates(arr);
        System.out.println("Duplicate Values are: " + result);
    }

    static List<Integer> findDuplicates(int[] nums){
        Map<Integer, Integer> frequency = new HashMap<>();
        List<Integer> duplicates = new ArrayList<>();

        for(int num : nums){
            int count  =frequency.getOrDefault(num, 0) + 1;
            frequency.put(num, count);

            if(count == 2){
                duplicates.add(num);
            }
        }
        return duplicates;
    }
}
