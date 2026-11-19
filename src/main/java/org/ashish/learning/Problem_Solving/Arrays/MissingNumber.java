package org.ashish.learning.Problem_Solving.Arrays;

import java.util.Arrays;

public class MissingNumber {
    static void main() {
        int[] arr = {3,0,1};
        int result = missingNumber(arr);
        System.out.println("Missing Number is " + result);
    }

    static int missingNumber(int[] nums){
       for(int candidate = 0; candidate < nums.length; candidate++){
           boolean found = false;

           for(int value : nums){
               if(value == candidate){
                   found = true;
                   break;
               }
           }
           if(!found){
               return candidate;
           }
       }
       return -1;
    }
}
