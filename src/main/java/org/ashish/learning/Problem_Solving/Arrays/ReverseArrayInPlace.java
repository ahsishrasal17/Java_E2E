package org.ashish.learning.Problem_Solving.Arrays;

public class ReverseArrayInPlace {
    public static void main(String[] args) {
        int[] arr = {25, 36, 19, 39, 85, 23};
        int result[] = reverseArray(arr);
        for(int i : result)
            System.out.print(i + " ");
    }

    static int[] reverseArray(int[] arr){
        int start = 0;
        int end = arr.length - 1;

        while(start<end){
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
        return arr;
    }
}
