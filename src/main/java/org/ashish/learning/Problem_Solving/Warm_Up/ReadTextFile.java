package org.ashish.learning.Problem_Solving.Warm_Up;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReadTextFile {
    static void main() {
        try {
            Path path = Path.of("C:\\Users\\Dell\\Desktop\\sample.txt.txt");
            String content = Files.readString(path);
            System.out.println(content);
        }catch (IOException e){
            System.out.println("Error Reading File: " + e.getMessage());
        }
    }
}
