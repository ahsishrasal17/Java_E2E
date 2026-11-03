package org.ashish.learning.Problem_Solving.Warm_Up;

        public class SumOfDigits {
            public static void main(String args[]) {
                int num = 123;
                int sum = 0;

                while (num > 0){
                    int remainder = num % 10;
                    num = num / 10;
                    sum = sum + remainder;
                }
                System.out.println("Sum of digits: " + sum);
    }
}
