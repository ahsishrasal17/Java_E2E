package org.ashish.learning.Problem_Solving.Arrays;

import java.util.Arrays;

public class RemoveDuplicatesFromSortedArray {
    static void main() {
        int[] arr = {0,0,1,1,1,2,2,3,3,4};
        int result = removeDuplicates(arr);
        System.out.println("Number of Unique Elements are: " + result);
    }

    static int removeDuplicates(int[] arr){
        int[] unique = new int[arr.length];
        int count  = 0;

        for(int value : arr){
            if(count == 0 || value != unique[count-1]){
                unique[count] = value;
                count++;
            }
        }
        System.arraycopy(unique,0,arr,0,count);
        System.out.println(Arrays.toString(arr));
        return count;
    }
}
