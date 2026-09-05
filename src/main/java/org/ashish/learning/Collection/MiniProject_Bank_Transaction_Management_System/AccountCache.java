package org.ashish.learning.Collection.MiniProject_Bank_Transaction_Management_System;

import java.util.LinkedHashMap;
import java.util.Map;

public class AccountCache<K,V> extends LinkedHashMap<K,V> {
    private int maxSize;

    public AccountCache(int maxSize){
        super(16, 0.75f, true);
        this.maxSize = maxSize;
    }

    @Override
    public boolean removeEldestEntry(Map.Entry<K,V> eldest){
        if (size() > maxSize){
            System.out.println("LRU: Evicting" + eldest.getKey());
            return true;
        }
        return false;
    }
}
