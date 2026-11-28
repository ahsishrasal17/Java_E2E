package org.ashish.learning.Problem_Solving.BinarySearch;

public class BinarySearch {
    static void main() {
        int[] arr = {-1,0,3,5,9,12};
        int target = 9;
        int result = binarySearch(arr, target);
        System.out.println(result);
    }

    static int binarySearch(int[] nums, int target){
        int left = 0;
        int right = nums.length - 1;

        while(left <= right){
            int mid = left + (right-left)/2;

            if(nums[mid] == target){
                return mid;
            }else if(nums[mid] < target){
                left = mid+1;
            }else {
                right = mid - 1;
            }
        }
        return -1;
    }
}
