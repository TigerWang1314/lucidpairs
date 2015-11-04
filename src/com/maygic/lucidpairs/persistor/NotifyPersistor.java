package com.maygic.lucidpairs.persistor;

import java.util.Map;

import com.maygic.lucidpairs.ChangeRecord;
import com.maygic.lucidpairs.LucidProps.SubProps;

public class NotifyPersistor extends AbstractPersistor {

    public NotifyPersistor(FileInteraction fileItn, SubProps props) {
        super(fileItn, props);
    }

    @Override
    public void persist(ChangeRecord cr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void load(Map<String, String> pairs) {
        // TODO Auto-generated method stub

    }
}
