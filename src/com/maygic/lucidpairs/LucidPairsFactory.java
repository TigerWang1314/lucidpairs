package com.maygic.lucidpairs;

import java.util.HashMap;
import java.util.Map;

public class LucidPairsFactory {

    private static final Map<String, LucidPairs> pairsMap = new HashMap<String, LucidPairs>();

    private static final Object lock = new Object();

    public static final LucidPairs pairs(String name) {
        synchronized (lock) {
            LucidPairs pairs = pairsMap.get(name);
            if (pairs == null) {
                pairs = new LucidPairs(name);
                pairs.load();
                pairsMap.put(name, pairs);
            }
            return pairs;
        }
    }
}
