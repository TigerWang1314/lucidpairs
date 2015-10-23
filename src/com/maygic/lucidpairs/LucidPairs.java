package com.maygic.lucidpairs;

import java.util.HashMap;
import java.util.Map;

public class LucidPairs {

    private String name;

    private Map<String, String> pairs = new HashMap<String, String>();

    LucidPairs(String name) {
        this.name = name;
    }

    private void lock() {

    }

    private void unlock() {

    }

    public void load() {

    }

    public void put(String name, String value) {

        lock();
        try {
            pairs.put(name, value);
        } finally {
            unlock();
        }
    }

    public void put(Map<String, String> pairs) {
        lock();
        try {
            pairs.putAll(pairs);
        } finally {
            unlock();
        }
    }

    public String get(String name) {
        lock();
        try {
            return pairs.get(name);
        } finally {
            unlock();
        }
    }

    public Map<String, String> getByPrefix(String namePrefix) {
        
        return null;
    }
}
