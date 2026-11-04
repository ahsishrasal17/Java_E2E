package org.ashish.learning.Problem_Solving.Strings;

public class RemoveWhiteSpaces {
    public static void main(String[] args) {
        String str = " J ava i s   awesome ";
        String result = removeWhiteSpaces(str);
        System.out.println("String after removing white spaces: " + result);
    }

    static String removeWhiteSpaces(String str){
        StringBuilder str1 = new StringBuilder();
        for(int i=0; i<str.length();i++){
            if(str.charAt(i) != ' '){
                str1.append(str.charAt(i));
            }
        }
        return new String(str1);
    }
}
