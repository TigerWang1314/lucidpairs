package com.maygic.lucidpairs.persistor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.maygic.lucidpairs.ChangeRecord;

public class PeriodPersistor extends AbstractPersistor {

    public PeriodPersistor(FileInteraction fileItn) {
        super(fileItn);
    }

    @Override
    public void persist(ChangeRecord cr) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<String, String> load() {
        // TODO Auto-generated method stub
        return null;
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
//            nonsyncPairs = new HashMap<String, String>(pairs); // TODO complate it later

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
                fileItn.persist(nonsyncPairs);

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
