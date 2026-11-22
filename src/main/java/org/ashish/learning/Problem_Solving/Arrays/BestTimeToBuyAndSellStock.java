package org.ashish.learning.Problem_Solving.Arrays;

public class BestTimeToBuyAndSellStock {
    public static void main(String[] args) {
        int[] arr = {7,1,5,3,6,4};
        int result = buyAndSell(arr);
        System.out.println(result);
    }

    static int buyAndSell(int[] nums){
        int highest = 0;


        for(int i=0; i<nums.length; i++){
            for(int j=i+1; j<nums.length; j++){
                    int current = nums[j] - nums[i];
                    if(current > highest){
                        highest = current;
                }
            }
        }
        return highest;
    }
}
