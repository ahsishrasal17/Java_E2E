package org.ashish.learning.Problem_Solving.Arrays;

import java.util.ArrayList;
import java.util.List;

public class KidsWithGreatestCandiesOptimal {
    static void main() {
        int[] arr = {2,3,5,1,3};
        int extraCandies = 3;
        List<Boolean> result = greatestCandies(arr,extraCandies);
        System.out.println(result);
    }

    static List<Boolean> greatestCandies(int[] nums, int extraCandies){
        int max = nums[0];

        for(int candies : nums){
            max = Math.max(max, candies);
        }

        List<Boolean> result = new ArrayList<>();
        for(int candies : nums){
            result.add(candies + extraCandies >= max);
        }
        return result;
    }
}
