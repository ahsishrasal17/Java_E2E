package org.ashish.learning.Problem_Solving.Arrays;

public class SecondLargestElement {
    public static void main(String[] args) {
        int[] arr = {25, 36, 19, 39, 85, 23};
        int result = secondLargestElement(arr);
        System.out.println(result);
    }

    static int secondLargestElement(int[] arr){
        int largest = Integer.MIN_VALUE;
        int secondLargest = Integer.MIN_VALUE;

        for(int i=0; i<arr.length; i++){
            if(arr[i] > largest) {
                secondLargest = largest;
                largest = arr[i];
            }else if(arr[i] > secondLargest && arr[i] != largest){
                secondLargest = arr[i];
            }
        }
        return secondLargest;
    }
}
