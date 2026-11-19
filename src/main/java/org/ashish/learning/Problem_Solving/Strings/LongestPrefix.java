package org.ashish.learning.Problem_Solving.Strings;

public class LongestPrefix {
    public static void main(String[] args) {
         String[] arr = {"flower", "flow", "flight"};
        String result = longestCommonPrefix(arr);
        System.out.println(result);
    }

    static String longestCommonPrefix(String[] arr){
        if(arr == null || arr.length == 0){
            return "";
        }

        String first = arr[0];
        String longestPrefix = "";

        for(int i=1; i<=first.length(); i++){
            String candidate = first.substring(0,i);
            boolean matchesEveryString = true;

            for(String word : arr){
                if(!word.startsWith(candidate)){
                    matchesEveryString = false;
                    break;
                }
            }
            if(!matchesEveryString){
                break;
            }

            longestPrefix = candidate;
        }
        return longestPrefix;
    }
}
