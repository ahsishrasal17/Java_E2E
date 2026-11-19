package org.ashish.learning.Problem_Solving.Arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindAllDuplicates {
    static void main() {
        int[] arr = {4, 3, 2, 7, 8, 2, 3, 1};
        int[] duplicates = findDuplicates(arr);

        System.out.println(Arrays.toString(duplicates));
    }

    static int[] findDuplicates(int[] arr){
        int[] duplicates = new int[arr.length];
        int duplicateCount = 0;

        for(int i=0; i<arr.length; i++){
            for(int j=i+1; j<arr.length;j++){
                if(arr[i] == arr[j]){
                    duplicates[duplicateCount] = arr[i];
                    duplicateCount++;
                    break;
                }
            }
        }
        return Arrays.copyOf(duplicates, duplicateCount);
    }
}
