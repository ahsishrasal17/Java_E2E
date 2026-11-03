package org.ashish.learning.Problem_Solving.Strings;

public class ReverseStringArray {
    public static void main(String[] args) {
        String str = "hello";
        char[] str1 = str.toCharArray();

        int start = 0;
        int end = str1.length-1;
        while (start < end){
            char temp = str1[start];
            str1[start] = str1[end];
            str1[end] = temp;
            start++;
            end--;
        }
        System.out.println(new String(str1));
    }
}
