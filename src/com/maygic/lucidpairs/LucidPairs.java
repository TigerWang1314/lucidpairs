package com.maygic.lucidpairs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.maygic.lucidpairs.persistor.Persistor;

public class LucidPairs {

    private final String name;

    private Map<String, String> pairs = new HashMap<String, String>();

    private Persistor persistor;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final WriteLock writeLock = lock.writeLock();
    private final ReadLock readLock = lock.readLock();

    LucidPairs(String name, Persistor persistor) {
        this.name = name;
        this.persistor = persistor;
    }

    void load() {

        // load datas
        persistor.load(pairs);
    }

    public void put(String name, String value) {
        writeLock.lock();
        try {
            pairs.put(name, value);

            // set change
            persist(ChangeRecord.put(name, value));
        } finally {
            writeLock.unlock();
        }
    }

    public void putAll(Map<String, String> pairs) {
        writeLock.lock();
        try {
            pairs.putAll(pairs);

            // set change
            persist(ChangeRecord.putAll(pairs));
        } finally {
            writeLock.unlock();
        }
    }

    public void clear() {
        writeLock.lock();
        try {
            pairs.clear();

            // set change
            persist(ChangeRecord.clear());
        } finally {
            writeLock.unlock();
        }
    }

    public void remove(String name) {
        writeLock.lock();
        try {
            pairs.remove(name);

            // set change
            persist(ChangeRecord.remove(name));
        } finally {
            writeLock.unlock();
        }
    }

    public void removeByPrefix(String prefix) {
        writeLock.lock();
        try {
            Iterator<String> it = pairs.keySet().iterator();
            while (it.hasNext()) {
                String name = it.next();
                if (name.startsWith(prefix)) {
                    it.remove();
                }
            }

            // set change
            persist(ChangeRecord.removeByPrefix(prefix));
        } finally {
            writeLock.unlock();
        }
    }

    public String get(String name) {
        readLock.lock();
        try {
            return pairs.get(name);
        } finally {
            readLock.unlock();
        }
    }

    public Map<String, String> getByPrefix(String namePrefix) {
        readLock.lock();
        try {
            Map<String, String> results = new HashMap<String, String>();
            for (Map.Entry<String, String> entry : pairs.entrySet()) {
                if (entry.getKey().startsWith(namePrefix)) {
                    results.put(entry.getKey(), entry.getValue());
                }
            }
            return results;
        } finally {
            readLock.unlock();
        }
    }

    private void persist(ChangeRecord cr) {
        persistor.persist(cr);
    }

    public void shutdown() {

    }
}
