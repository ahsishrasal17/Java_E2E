package org.ashish.learning.Problem_Solving.Warm_Up;

public class Plaindrome {
    public static void main(String[] args) {
        int num = 121;
        boolean result = isPalindrome(num);
        if (result){
            System.out.println(num + " is a palindrome number.");
        } else {
            System.out.println(num + " is not a palindrome number.");
        }
    }

        static boolean isPalindrome(int num) {

            int reversedNum = 0;
            int remainder = 0;
            int originalNum = num;

        while(num > 0){
            remainder = num % 10;
            reversedNum = reversedNum * 10 + remainder;
            num = num/10;
        }

        if (originalNum == reversedNum){
            return true;
        } else {
            return false;
        }
    }
}
