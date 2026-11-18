package org.ashish.learning.Problem_Solving.Arrays;

public class RemoveDuplicatesFromSortedArrayOptimal {
    static void main() {
        int[] arr = {0,0,1,1,1,2,2,3,3,4};
        int result = removeDuplicates(arr);
        System.out.println("Number of Unique Elements are: " + result);
    }

    static int removeDuplicates(int[] arr){
       int write = 0;

       for(int read=1; read<arr.length;read++){
           if(arr[read] != arr[write]){
               write++;
               arr[write] = arr[read];
           }
       }
       return write+1;
    }
}
