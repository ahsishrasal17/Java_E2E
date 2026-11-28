package org.ashish.learning.Problem_Solving.Arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ThreeSumOptimal {
    static void main() {
        int[] arr = {-1,0,1,2,-1,-4};
        List<List<Integer>> result = threeSum(arr);
        System.out.println(result);
    }

    static List<List<Integer>> threeSum(int[] nums){

        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);

        for(int fix = 0; fix < nums.length; fix++){
            if(nums[fix] > 0){
                break;
            }
            if(fix > 0 && nums[fix] == nums[fix-1]){
                continue;
            }

            int left = fix + 1;
            int right = nums.length-1;

            while(left<right){
                int sum = nums[fix] + nums[left] + nums[right];
                if(sum == 0){
                    List<Integer> triplet = new ArrayList<>(Arrays.asList(nums[fix], nums[left], nums[right]));
                        result.add(triplet);
                    left++;
                    right--;
                    while (left < right && nums[left] == nums[left - 1]) {
                        left++;
                    }
                    while (left < right && nums[right] == nums[right + 1]) {
                        right--;
                    }
                }else if(sum<0){
                    left++;
                }else{
                    right--;
                }
            }
        }
        return result;
    }
}
