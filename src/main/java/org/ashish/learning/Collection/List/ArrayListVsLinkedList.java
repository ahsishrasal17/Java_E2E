package org.ashish.learning.Collection.List;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ArrayListVsLinkedList {
    static void main() {
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();

        long arrayStart = System.currentTimeMillis();
        for(int i=0; i<100000; i++){
            arrayList.add(0,i);
        }
        System.out.println(System.currentTimeMillis() - arrayStart);

        long listStart = System.currentTimeMillis();
        for (int i=0; i<100000; i++){
            linkedList.add(0,i);
        }
        System.out.println(System.currentTimeMillis() - listStart);
    }
}
