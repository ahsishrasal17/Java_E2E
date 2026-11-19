package org.ashish.learning.Problem_Solving.Strings;

public class CountWords {
    public static void main(String[] args) {
        String str = "My Name Is Ashish";
        int result = countWords(str);
        System.out.println("Number of words in the string: " + result);
    }

    static int countWords(String str){
        int count = 0;
        boolean previousWasSpace = true;

        for(int i=str.length()-1; i>=0;i--){
            if(str.charAt(i) == ' '){
                previousWasSpace = true;
            }else{
                if(previousWasSpace){
                    count++;
                }
                previousWasSpace = false;
            }
        }
        return count;
    }
}
