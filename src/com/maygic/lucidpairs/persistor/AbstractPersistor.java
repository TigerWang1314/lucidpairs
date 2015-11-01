package com.maygic.lucidpairs.persistor;

public abstract class AbstractPersistor implements Persistor {

    protected FileInteraction fileItn;

    public AbstractPersistor(FileInteraction fileItn) {
        this.fileItn = fileItn;
    }

}
