package org.ashish.learning.Collection.List;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListComaprisonDemo {

    static void main() {
        List<String> arrayList = new ArrayList<>();
        List<String> linkedList = new LinkedList<>();

        arrayList.add("Ashish");
        arrayList.add("Pratik");
        arrayList.add(0, "Akshay");

        linkedList.add("Ashish");
        linkedList.add("Pratik");
        linkedList.add(0,"Akshay");

        System.out.println("ArrayList: " + arrayList);
        System.out.println("LinkedList: " + linkedList);

        System.out.println("ArrayList get(1) : " + arrayList.get(1));
        System.out.println("LinkedList get(1) : " + linkedList.get(1));

        LinkedList<String> queue = new LinkedList<>();
        queue.addLast("First Case");
        queue.addLast("Second Case");
        queue.addLast("Third Case");
        System.out.println("Queue: " + queue);
        System.out.println("Processing Queue: " + queue.removeFirst());
    }
}
