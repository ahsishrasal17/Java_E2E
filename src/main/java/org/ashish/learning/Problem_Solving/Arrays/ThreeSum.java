package org.ashish.learning.Problem_Solving.Arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ThreeSum {
    static void main() {
        int[] arr = {-1,0,1,2,-1,-4};
        List<List<Integer>> result = threeSum(arr);
        System.out.println(result);
    }

    static List<List<Integer>> threeSum(int[] nums){

        List<List<Integer>> result = new ArrayList<>();
        for(int i=0; i<nums.length;i++){
            for(int j=i+1; j<nums.length;j++){
                for(int k=j+1; k<nums.length;k++){
                    if(nums[i] + nums[j] + nums[k] == 0){
                        List<Integer> triplet = new ArrayList<>(Arrays.asList(nums[i],nums[j],nums[k]));

                        Collections.sort(triplet);

                        if(!result.contains(triplet)){
                            result.add(triplet);
                        }
                    }
                }
            }
        }
        return result;
    }
}
