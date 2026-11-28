package org.ashish.learning.Problem_Solving.Arrays;

import java.util.Arrays;

public class MergeSortedArray {
    public static void main(String[] args) {
        int[] arr1 = {1,2,3,0,0,0};
        int[] arr2 = {2,5,6};
        int m = 3;
        int n = 3;
        int result[] = mergeSortedArray(arr1,arr2,m,n);
        System.out.println(Arrays.toString(result));
    }

    static int[] mergeSortedArray(int[] nums1, int[] nums2, int m, int n) {
        int[] merged = new int[m + n];
        int count = 0;

        for(int i=0; i<m; i++){
            merged[count] = nums1[i];
            count++;
        }

        for(int j=0; j<n; j++){
            merged[count] = nums2[j];
            count++;
        }

        Arrays.sort(merged);
        System.arraycopy(merged,0,nums1,0,m+n);
        return nums1;
    }
}
