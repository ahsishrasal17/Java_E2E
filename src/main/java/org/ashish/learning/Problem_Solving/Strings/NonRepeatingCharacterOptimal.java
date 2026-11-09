package org.ashish.learning.Problem_Solving.Strings;

import java.util.HashMap;
import java.util.Map;

public class NonRepeatingCharacterOptimal {
    public static void main(String args[]) {
        String str = "aB!bA";
        char result = firstNonRepeatingCharacter(str);
        System.out.println(result);
    }

    static char firstNonRepeatingCharacter(String str){
        Map<Character, Integer> countMap = new HashMap<>();

        for(int i=0; i<str.length();i++){
            char ch = str.charAt(i);

            countMap.put(ch, countMap.getOrDefault(ch, 0) + 1);
        }

        for(int i=0; i< str.length(); i++){
            if(countMap.get(str.charAt(i)) == 1){
                return str.charAt(i);
            }
        }
        return '\0';
    }
}
