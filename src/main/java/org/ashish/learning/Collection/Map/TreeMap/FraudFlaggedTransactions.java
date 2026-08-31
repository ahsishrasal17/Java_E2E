package org.ashish.learning.Collection.Map.TreeMap;

import java.sql.SQLOutput;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class FraudFlaggedTransactions {
    static void main() {
        Map<Double, String> flaggedTransactions = new TreeMap<>();
        flaggedTransactions.put(45000.0, "TXN-9001");
        flaggedTransactions.put(250000.0, "TXN-9002");
        flaggedTransactions.put(99999.0, "TXN-9003");
        flaggedTransactions.put(105000.0, "TXN-9004");
        flaggedTransactions.put(500000.0, "TXN-9005");

        TreeMap<Double, String> sortedMap = (TreeMap<Double, String>) flaggedTransactions;

        SortedMap<Double, String> highValues = sortedMap.tailMap(100000.0);
        System.out.println("High Value Transactions: " + highValues);

        System.out.println("Highest Risk Flag: " + highValues.lastEntry());

        System.out.println("Closet Flag at or Above 100000: " + sortedMap.ceilingKey(100000.0));
    }
}
