package com.maygic.lucidpairs.persistor;

import java.util.Map;

import com.maygic.lucidpairs.ChangeRecord;

public class RealTimePersistor extends AbstractPersistor {

    private Map<String, String> pairs;

    public RealTimePersistor(FileInteraction fileItn) {
        super(fileItn);
    }

    @Override
    public void persist(ChangeRecord cr) {
        fileItn.persist(pairs);
    }

    @Override
    public void load(Map<String, String> pairs) {
        this.pairs = pairs;
        fileItn.load(pairs);
    }

}
