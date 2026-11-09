package org.ashish.learning.Problem_Solving.Strings;

public class NonRepeatingCharacter {
    public static void main(String args[]) {

        String str = "My name is Ashish";
        Character result = nonRepeatingCharacter(str);
        System.out.println("First Non-Repeating Character: " + result);
    }

    static Character nonRepeatingCharacter(String s){
        int[] counts = new int[26];
        String str = s.toLowerCase();
        for(int i=0; i<str.length(); i++){
            if(str.charAt(i) != ' '){
                counts[str.charAt(i) - 'a']++;
            }
        }
       for(int i=0; i<str.length();i++){
           if(str.charAt(i) != ' ' && counts[str.charAt(i) - 'a'] == 1){
               return str.charAt(i);
           }
       }
       return null;
    }
}
