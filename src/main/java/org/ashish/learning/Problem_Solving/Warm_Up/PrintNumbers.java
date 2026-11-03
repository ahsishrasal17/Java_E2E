package org.ashish.learning.Problem_Solving.Warm_Up;

public class PrintNumbers {
    static void main() {
        int n = 10;

        //For Loop
        /*for(int i=1; i<=n; i++){
            System.out.println(i);
        }*/

        int i = 1;

        //While Loop
        while(i<=n){
            System.out.println(i);
            i++;
        }

        //Do While Loop
        do{
            System.out.println(i);
            i++;
        }while(i<=n);
    }
}
