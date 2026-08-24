package org.ashish.learning.Collection.Map;

public class HashMapDetails {
    public static void main(String[] args) {
        // Negative hash codes and bucket indices
        String key = "Praktik";
        int hash = key.hashCode();
        System.out.println("Hash: " + hash);
        System.out.println("Bucket (raw % 16): " + (hash % 16));  // Can be negative
        System.out.println("Bucket (Math.abs): " + (Math.abs(hash) % 16));  // Always positive

        // Identity vs content hash
        String s1 = new String("Ashish");
        String s2 = new String("Ashish");

        System.out.println("\nIdentity hash s1: " + System.identityHashCode(s1));
        System.out.println("Identity hash s2: " + System.identityHashCode(s2));
        System.out.println("Are identity hashes equal? " +
                (System.identityHashCode(s1) == System.identityHashCode(s2)));

        System.out.println("\nContent hash s1: " + s1.hashCode());
        System.out.println("Content hash s2: " + s2.hashCode());
        System.out.println("Are content hashes equal? " + (s1.hashCode() == s2.hashCode()));

        System.out.println("\nEquals? " + s1.equals(s2));  // true — content comparison
        System.out.println("Identity (==)? " + (s1 == s2));  // false — different objects
    }
}
