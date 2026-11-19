package org.ashish.learning.Problem_Solving.Strings;

public class StringToInt {
    public static void main(String args[]) {
        String str = "123";
        int result = stringToInt(str);
        System.out.println("String to Integer: " + result);
    }

    static int stringToInt(String str){
        int result = 0;

        for(int i=0; i<str.length(); i++){
            char ch = str.charAt(i);
            int num = ch - '0';
            result = result * 10 + num;
        }

        return result;
        }
}
