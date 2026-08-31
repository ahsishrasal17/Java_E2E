package org.ashish.learning.Collection.Map.TreeMap;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class TreeMapDemo {

    static void main() {
        TreeMap<String, Double> accountBalances = new TreeMap<>();
        accountBalances.put("ACC003", 3000.0);
        accountBalances.put("ACC001", 5000.0);
        accountBalances.put("ACC002", 2000.0);
        accountBalances.put("ACC004", 1000.0);

        System.out.println(accountBalances);
        System.out.println(accountBalances.firstKey());
        System.out.println(accountBalances.lastKey());
        System.out.println(accountBalances.firstEntry());
        System.out.println(accountBalances.lastEntry());
        System.out.println(accountBalances.get("ACC005"));
        System.out.println(accountBalances.headMap("ACC002"));
        System.out.println(accountBalances.tailMap("ACC002"));
        System.out.println(accountBalances.ceilingKey("ACC003"));
        System.out.println(accountBalances.floorKey("ACC003"));
    }
}
