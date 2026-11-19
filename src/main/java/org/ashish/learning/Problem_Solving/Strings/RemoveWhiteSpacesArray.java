package org.ashish.learning.Problem_Solving.Strings;

public class RemoveWhiteSpacesArray {
    public static void main(String[] args) {
        String str = " Jav a  i s    superb  ";
        String result = removeWhiteSpaces(str);
        System.out.println("Original: '" + str + "'");
        System.out.println("Modified: '" + result + "'");
    }

    static String removeWhiteSpaces(String str){
        char[] arr = str.toCharArray();
        int writeIndex = 0;
        for(int i=0; i<arr.length; i++){
            if(arr[i] != ' '){
                arr[writeIndex] = arr[i];
                writeIndex++;
            }
        }
        return new String(arr, 0, writeIndex);
    }
}
