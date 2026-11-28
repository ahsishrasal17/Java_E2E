package org.ashish.learning.Problem_Solving.Arrays;

public class NumberOfStepsToReduceNumberToZero {
    static void main() {
        int num = 14;
        int result = steps(num);
        System.out.println(result);
    }

    static int steps(int num){
        int step = 0;

        while(num > 0){
            if(num % 2 == 0){
                num /= 2;
                step++;
            }else {
                num -= 1;
                step++;
            }
        }
        return step;
    }
}
