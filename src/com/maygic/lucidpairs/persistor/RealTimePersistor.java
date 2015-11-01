package com.maygic.lucidpairs.persistor;

import java.util.Map;

import com.maygic.lucidpairs.ChangeRecord;

public class RealTimePersistor extends AbstractPersistor {

    public RealTimePersistor(FileInteraction fileItn) {
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

}
