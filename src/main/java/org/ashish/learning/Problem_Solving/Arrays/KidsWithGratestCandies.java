package org.ashish.learning.Problem_Solving.Arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KidsWithGratestCandies {
    static void main() {
        int[] arr = {2,3,5,1,3};
        int extraCandies = 3;
        List<Boolean> result = greatestCandies(arr, extraCandies);
        System.out.println(result);
    }

    static List<Boolean> greatestCandies(int[] nums, int extraCandies){
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        int max = sorted[sorted.length - 1];
        List<Boolean> result = new ArrayList<>();

        for(int i=0; i< nums.length; i++){
            if(nums[i] + extraCandies >= max){
                result.add(true);
            }else {
                result.add(false);
            }
        }
        return result;
    }
}
