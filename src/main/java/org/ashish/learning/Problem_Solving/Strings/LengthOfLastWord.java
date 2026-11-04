package org.ashish.learning.Problem_Solving.Strings;

public class LengthOfLastWord {
    public static void main(String[] args) {
        String str = "Hello World  ";
        int result = lengthOfLastWord(str);
        System.out.println("Length of last word in the string: " + result);
    }

    static int lengthOfLastWord(String s){
        int count= 0;

        for(int i=s.length()-1; i>=0; i--){
            if(s.charAt(i) == ' '){
                if(count > 0){
                    break;
                }
            }else{
                count++;
            }
        }
        return count;
    }

}
