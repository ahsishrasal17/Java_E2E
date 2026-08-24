package org.ashish.learning.Collection.Map;

import java.util.HashMap;
import java.util.Map;

public class HashMapDemo {

    static void main() {
        Map<String, Integer> map = new HashMap<>();
        map.put("Ashish", 28);
        map.put("Pratik", 26);
        map.put("Ashish",29);

        System.out.println(map.get("Ashish"));
        System.out.println(map.size());
        System.out.println(map.containsKey("Darshan"));
    }
}
