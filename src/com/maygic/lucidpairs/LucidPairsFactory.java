package com.maygic.lucidpairs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LucidPairsFactory {

    private static final Map<String, LucidPairs> pairsMap = new HashMap<String, LucidPairs>();

    private static final Object lock = new Object();

    private static Prop prop;

    public static final LucidPairs pairs(String name) {
        synchronized (lock) {
            LucidPairs pairs = pairsMap.get(name);
            if (pairs == null) {

                // make FileInteraction
                try {
                    FileInteraction file = new FileInteraction(prop.baseFolder, name);
                    pairs = new LucidPairs(name, prop.persistSync, file);
                    pairs.load();
                    pairsMap.put(name, pairs);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return pairs;
        }
    }

    class Prop {
        String baseFolder;
        boolean persistSync;
    }

}
