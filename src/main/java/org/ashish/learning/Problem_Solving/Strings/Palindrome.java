package org.ashish.learning.Problem_Solving.Strings;

public class Palindrome {
    public static void main(String args[]) {
        String str = "A man, a plan, a canal: Panama";
        boolean result = isPalimdrome(str);
        System.out.println("Is the string a palindrome? " + result);
    }

    static boolean isPalimdrome(String str){
        StringBuilder cleaned = new StringBuilder();
        for(int i=0; i<str.length();i++){
            if(Character.isLetterOrDigit(str.charAt(i))){
                cleaned.append(Character.toLowerCase(str.charAt(i)));
            }
        }
        int left = 0;
        int right = cleaned.length()-1;

        while(left < right){
            if(cleaned.charAt(left) != cleaned.charAt(right)){
                return false;
            }else{
                left++;
                right--;
            }
        }
        return true;
    }
}
