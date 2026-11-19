package org.ashish.learning.Problem_Solving.Strings;

public class Anagram {
    public static void main(String args[]) {
        String s = "silent";
        String t = "listen";
        boolean result = isAnagram(s, t);
        System.out.println("Are the two strings anagrams? " + result);
    }

    static boolean isAnagram(String s, String t){
        int sum1 = 0;
        int sum2 = 0;

        if(s.length() != t.length()){
            return false;
        }

        int[] freq = new int[26];

        for(int i=0; i<s.length();i++){
            freq[s.charAt(i) - 'a']++;
            freq[t.charAt(i) - 'a']--;
        }

        for(int count : freq){
            if (count !=0){
                return false;
            }
        }
        return true;
    }
}
