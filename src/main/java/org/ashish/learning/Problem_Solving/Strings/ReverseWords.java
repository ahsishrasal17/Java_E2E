package org.ashish.learning.Problem_Solving.Strings;

import java.util.ArrayList;
import java.util.List;

public class ReverseWords {
    static void main() {
        String str = "Java is awesome";
        String result = reverseWords(str);
        System.out.println("Reversed Words: " + result);
    }

    static String reverseWords(String s){
        StringBuilder word = new StringBuilder();
        List<String> list = new ArrayList<>();

        for(int i=0; i<s.length(); i++){
            char ch = s.charAt(i);

            if(ch != ' '){
                word.append(ch);
            }else if(word.length() > 0){
                list.add(word.toString());
                word.setLength(0);
            }
        }
        if(word.length() > 0){
            list.add(word.toString());
        }

        StringBuilder result = new StringBuilder();
        for(int i=list.size()-1; i>=0; i--){
            result.append(list.get(i));
            if(i > 0){
                result.append(" ");
            }
        }
        return result.toString();
    }
}
