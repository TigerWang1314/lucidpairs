package com.maygic.lucidpairs.persistor;

import java.util.Map;

import com.maygic.lucidpairs.LucidProps;

public abstract class AbstractPersistor implements Persistor {

    protected FileInteraction fileItn;
    protected LucidProps.SubProps props;

    public AbstractPersistor(FileInteraction fileItn, LucidProps.SubProps props) {
        this.fileItn = fileItn;
    }

    @Override
    public void load(Map<String, String> pairs) {
        fileItn.load(pairs);
    }

    protected String getProp(String name) {
        return props.getProp(name);
    }

}
