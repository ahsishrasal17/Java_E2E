package org.ashish.learning.Problem_Solving.Strings;

public class MaximumLength {
    public static void main(String[] args) {
        String str = "Java    is awesome";
        int result = maximumLength(str);
        System.out.println("Maximum length of a word in the string: " + result);
    }

    static int maximumLength(String str){
        int maxLength = 0;
        int currLength = 0;

        for(int i=0; i<str.length();i++){


            if(str.charAt(i) == ' '){
                if(currLength > maxLength){
                    maxLength = currLength;
                }
                currLength = 0;
            }else{
                currLength++;
            }
        }
        if(currLength > maxLength){
            maxLength = currLength;
        }
        return maxLength;
    }

}
