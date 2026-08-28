package org.ashish.learning.Collection.Map.HashMap;

public class BucketCalculation {
    static void main() {
        String key1 = "Ashish";
        String key2 = "Pratik";

        int hash1 = key1.hashCode();
        int hash2 = key2.hashCode();

        int bucketindex1 = hash1 % 16;
        int bucketindex2 = hash2 % 16;

        System.out.println(hash1);
        System.out.println(hash2);

        System.out.println(bucketindex1);
        System.out.println(bucketindex2);
    }
}
