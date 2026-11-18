package org.ashish.learning.Problem_Solving.Arrays;

public class LargestElement {
    static void main() {
        int[] arr = {25,36,19,39,85,23};
        int result = largestElement(arr);
        System.out.println(result);
    }

    static int largestElement(int[] arr){
        if(arr.length == 0){
            return 0;
        }

        int largest = arr[0];

        for(int i=1; i<arr.length;i++){
            if(arr[i] > largest){
                largest = arr[i];
            }
        }
        return largest;
    }
}
