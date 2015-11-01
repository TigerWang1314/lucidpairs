package com.maygic.lucidpairs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.maygic.lucidpairs.persistor.Persistor;
import com.maygic.lucidpairs.persistor.PersistorFactory;

public class LucidPairsFactory {

    private static final Map<String, LucidPairs> pairsMap = new HashMap<String, LucidPairs>();

    private static final Object lock = new Object();

    public static final LucidPairs pairs(String name) {
        synchronized (lock) {
            LucidPairs pairs = pairsMap.get(name);
            if (pairs == null) {

                // make FileInteraction
                try {
                    Persistor persistor = PersistorFactory.createPersistor(name);
                    pairs = new LucidPairs(name, persistor);
                    pairs.load();
                    pairsMap.put(name, pairs);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return pairs;
        }
    }

}
