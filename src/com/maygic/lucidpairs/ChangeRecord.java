package com.maygic.lucidpairs;

import java.util.Iterator;
import java.util.Map;

public interface ChangeRecord {

    public static ChangeRecord put(String name, String value) {
        return new Put(name, value);
    }

    public static ChangeRecord putAll(Map<String, String> pairs) {
        return new PutAll(pairs);
    }

    public static ChangeRecord remove(String name) {
        return new Remove(name);
    }

    public static ChangeRecord clear() {
        return new Clear();
    }

    public static ChangeRecord removeByPrefix(String prefix) {
        return new RemoveByPrefix(prefix);
    }

    public void change(Map<String, String> pairs);

    static class Put implements ChangeRecord {

        String name;
        String value;

        private Put(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public void change(Map<String, String> pairs) {
            pairs.put(name, value);
        }
    }

    static class PutAll implements ChangeRecord {

        Map<String, String> pairs;

        public PutAll(Map<String, String> pairs) {
            this.pairs = pairs;
        }

        @Override
        public void change(Map<String, String> pairs) {
            pairs.putAll(pairs);
        }
    }

    static class Remove implements ChangeRecord {

        String name;

        private Remove(String name) {
            this.name = name;
        }

        @Override
        public void change(Map<String, String> pairs) {
            pairs.remove(name);
        }
    }

    static class Clear implements ChangeRecord {

        @Override
        public void change(Map<String, String> pairs) {
            pairs.clear();
        }
    }

    static class RemoveByPrefix implements ChangeRecord {

        String prefix;

        private RemoveByPrefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void change(Map<String, String> pairs) {
            Iterator<String> it = pairs.keySet().iterator();
            while (it.hasNext()) {
                String name = it.next();
                if (name.startsWith(prefix)) {
                    it.remove();
                }
            }
        }
    }
}
