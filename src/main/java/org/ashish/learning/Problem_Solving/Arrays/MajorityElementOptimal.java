package org.ashish.learning.Problem_Solving.Arrays;

public class MajorityElementOptimal {
    static void main() {
        int[] arr = {2,2,1,1,1,2,2,};
        int result = majorityElement(arr);
        System.out.println(result);
    }

    static int majorityElement(int[] nums){
        int candidate = 0;
        int count = 0;

        for(int num : nums){
            if(count == 0){
                candidate = num;
            }

            if(num == candidate){
                count++;
            }else{
                count--;
            }
        }
        return candidate;
    }
}
