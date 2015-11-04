package com.maygic.lucidpairs.persistor;

import java.util.Map;

import com.maygic.lucidpairs.ChangeRecord;
import com.maygic.lucidpairs.LucidProps.SubProps;

public class RealTimePersistor extends AbstractPersistor {

    private Map<String, String> pairs;

    public RealTimePersistor(FileInteraction fileItn, SubProps props) {
        super(fileItn, props);
    }

    @Override
    public void persist(ChangeRecord cr) {
        fileItn.persist(pairs);
    }

    @Override
    public void load(Map<String, String> pairs) {
        super.load(pairs);
        this.pairs = pairs;
    }
}
