package com.maygic.lucidpairs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class LucidPairs {

    private final String name;

    private Map<String, String> pairs = new HashMap<String, String>();

    private FileInteraction fileIteraction;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final WriteLock writeLock = lock.writeLock();
    private final ReadLock readLock = lock.readLock();

    private final boolean persistSync;

    private NonSyncPersistThread nonSyncTread = new NonSyncPersistThread();

    LucidPairs(String name, boolean persistSync, FileInteraction fileIteraction) {
        this.name = name;
        this.persistSync = persistSync;
        this.fileIteraction = fileIteraction;
    }

    public void load() {

        // load datas
        pairs = fileIteraction.load();

        nonSyncTread.start();
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
        if (persistSync) {
            fileIteraction.persist(pairs);
        } else {

        }
    }

    public void shutdown() {

    }

    /**
     * the thread which persist pairs with nonsync mode
     * 
     * @author TigerWang1314
     *
     */
    class NonSyncPersistThread extends Thread {

        private List<ChangeRecord> list1 = new ArrayList<ChangeRecord>();
        private List<ChangeRecord> list2 = new ArrayList<ChangeRecord>();

        private int listnum = 1;

        private volatile boolean running;

        private Map<String, String> nonsyncPairs;

        private int persistPeriod;

        void setChange(ChangeRecord cr) {
            getAddList().add(cr);
        }

        @Override
        public void run() {

            // sync pairs
            nonsyncPairs = new HashMap<String, String>(pairs);

            // start run
            while (running) {

                // change list num
                changeNum();

                // set change
                List<ChangeRecord> persistRecord = getPersistList();
                for (ChangeRecord cr : persistRecord) {
                    cr.change(nonsyncPairs);
                }

                // persist
                fileIteraction.persist(nonsyncPairs);

                persistRecord.clear();

                try {
                    TimeUnit.SECONDS.sleep(persistPeriod);
                } catch (InterruptedException e) {
                }
            }
        }

        private List<ChangeRecord> getAddList() {
            return listnum == 1 ? list1 : list2;
        }

        private List<ChangeRecord> getPersistList() {
            return listnum == 1 ? list2 : list1;
        }

        private void changeNum() {
            if (listnum == 1) {
                listnum = 2;
            } else {
                listnum = 1;
            }
        }
    }
}
