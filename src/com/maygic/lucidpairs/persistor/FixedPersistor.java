package com.maygic.lucidpairs.persistor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.maygic.lucidpairs.ChangeRecord;
import com.maygic.lucidpairs.LucidProps;
import com.maygic.lucidpairs.LucidProps.SubProps;

public class FixedPersistor extends AbstractPersistor {

    private static final String PROP_PERIOD = "period";

    private static final int DEFAULT_PERIOD = 10;

    private Map<String, String> fixedPairs;

    private FixedPersistThread persistThread;

    public FixedPersistor(FileInteraction fileItn, SubProps props) {
        super(fileItn, props);
    }

    @Override
    public void persist(ChangeRecord cr) {
        persistThread.addChange(cr);
    }

    @Override
    public void load(Map<String, String> pairs) {

        // load data from file
        fileItn.load(pairs);

        // copy data to fix-pairs
        fixedPairs = new HashMap<String, String>(pairs);

        // get period from properties
        int period = DEFAULT_PERIOD;
        String periodInfo = getProp(PROP_PERIOD);
        if (periodInfo != null) {
            try {
                period = Integer.valueOf(periodInfo);
            } catch (NumberFormatException e) {
                throw new LucidPairsLoadException(
                        "error prop " + PROP_PERIOD + " value " + periodInfo + ", it must be integer");
            }
            if (period <= 0) {
                throw new LucidPairsLoadException("period must be greater than 0");
            }
        }

        // start persist-thread
        persistThread = new FixedPersistThread(period);
        persistThread.start();
    }

    /**
     * the thread which persist pairs with nonsync mode
     * 
     * @author TigerWang1314
     *
     */
    class FixedPersistThread extends Thread {

        private List<ChangeRecord> list1 = new ArrayList<ChangeRecord>();
        private List<ChangeRecord> list2 = new ArrayList<ChangeRecord>();

        private int listnum = 1;

        private volatile boolean running;

        private final int period;

        FixedPersistThread(int period) {
            this.period = period;
        }

        void addChange(ChangeRecord cr) {
            getAddList().add(cr);
        }

        @Override
        public void run() {

            // start run
            while (running) {

                // change list num
                changeNum();

                // set change
                List<ChangeRecord> persistRecord = getPersistList();
                for (ChangeRecord cr : persistRecord) {
                    cr.change(fixedPairs);
                }

                // persist
                fileItn.persist(fixedPairs);

                persistRecord.clear();

                try {
                    TimeUnit.SECONDS.sleep(period);
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
