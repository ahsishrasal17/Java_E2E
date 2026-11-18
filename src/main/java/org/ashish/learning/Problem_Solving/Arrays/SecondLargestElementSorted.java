package org.ashish.learning.Problem_Solving.Arrays;

import java.util.Arrays;

public class SecondLargestElementSorted {
    static void main() {
        int[] arr = {25, 36, 19, 39, 85, 23};
        int result = secondLargestElement(arr);
        System.out.println(result);
    }

    static int secondLargestElement(int[] arr){
        Arrays.sort(arr);
        int largest = arr[arr.length - 1];

        for(int i = arr.length -2; i >=0; i++){
            if(arr[i] != largest){
               return arr[i];
            }
        }
        return 0;
    }
}
