package com.maygic.lucidpairs.persistor;

import java.io.IOException;

import com.maygic.lucidpairs.LucidProps;
import com.maygic.lucidpairs.LucidProps.SubProps;

public class PersistorFactory {

    public static final Persistor createPersistor(String name) throws IOException {

        FileInteraction fileItn = new FileInteraction(LucidProps.getBaseFolder(), name);

        Persistor persistor = null;
        SubProps subProps = LucidProps.getSubProps(name);
        if (LucidProps.TYPE_NOTIFY.equals(subProps.getType())) {
            persistor = new NotifyPersistor(fileItn, subProps);
        } else if (LucidProps.TYPE_FIXED.equals(subProps.getType())) {
            persistor = new FixedPersistor(fileItn, subProps);
        } else if (LucidProps.TYPE_REALTIME.equals(subProps.getType())) {
            persistor = new RealTimePersistor(fileItn, subProps);
        }

        return persistor;
    }
}
