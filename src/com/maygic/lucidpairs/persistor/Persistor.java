package com.maygic.lucidpairs.persistor;

import java.util.Map;

import com.maygic.lucidpairs.ChangeRecord;

public interface Persistor {

    public void persist(ChangeRecord cr);

    public void load(Map<String, String> pairs);
}
