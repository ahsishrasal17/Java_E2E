

package org.ashish.learning.Problem_Solving.Strings;



public class VowelsCount {


    static void main() {
        String str = "Hello";
        int result = countVowels(str);
        System.out.println("Number of vowels in '" + str + "': " + result);
    }


    static int countVowels(String str) {
        String lower = str.toLowerCase();
        int count = 0;
        for (int i = 0; i < lower.length(); i++) {
            if (lower.charAt(i) == 'a' || lower.charAt(i) == 'e' || lower.charAt(i) == 'i' || lower.charAt(i) == 'o' || lower.charAt(i) == 'u') {
                count++;
            }
        }
        return count;
    }

}