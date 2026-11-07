package org.ashish.learning.Problem_Solving.Strings;

public class ReverseWordsOptimal {
    static void main() {
        String str = "Java is awesome";
        String reversed = reverseWords(str);
        System.out.println("Reversed Words: " + reversed);
    }

    static String reverseWords(String str){
        String[] words = str.trim().split("\\s+");

        StringBuilder result = new StringBuilder();

        for(int i = words.length - 1; i>=0; i--){
            result.append(words[i]);

            if(i > 0){
                result.append(" ");
            }
        }
        return result.toString();
    }
}
