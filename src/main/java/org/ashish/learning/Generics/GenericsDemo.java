package org.ashish.learning.Generics;

import java.util.ArrayList;
import java.util.List;

public class GenericsDemo {
    public static void main(String[] args) {
        List list1 = new ArrayList();
        list1.add("hi");
        list1.add(12);

        for (Object obj : list1) {
            System.out.println(((String) obj).toUpperCase());
        }
    }
}
