package org.ashish.learning.Problem_Solving.Warm_Up;

public class ReverseNumber {
        public static void main(String args[]) {
        int num = -12345;
        int reversed = 0;
        int sign = 1;

        if (num < 0){
            sign = -1;
            num = -num;
        }

        while(num>0) {
            int remainder = num % 10;
            reversed = reversed * 10 + remainder;
            num = num / 10;
        }
        System.out.println(sign * reversed);
    }
}
