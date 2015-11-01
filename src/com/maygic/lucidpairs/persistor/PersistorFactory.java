package com.maygic.lucidpairs.persistor;

import java.io.IOException;
import java.util.Map;

import com.maygic.lucidpairs.LucidProps;

public class PersistorFactory {

    public static final Persistor createPersistor(String name) throws IOException {

        FileInteraction file = new FileInteraction(LucidProps.getBaseFolder(), name);

        Persistor persistor = null; // TODO create new instance with props
        return persistor;
    }
}
