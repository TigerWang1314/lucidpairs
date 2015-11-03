package com.maygic.lucidpairs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maygic.lucidpairs.persistor.LucidPairsLoadException;

public class LucidProps {

    public static final String TYPE_NOTIFY = "notify";
    public static final String TYPE_PERIOD = "period";
    public static final String TYPE_REALTIME = "realtime";

    public static final String SUB_DEFAULT = "default";

    private static final String BASE_FOLDER = "baseFolder";

    private static final String PERSIST_TYPE = "persistType";

    private static String baseFolder;

    private static Map<String, SubProps> subs = new HashMap<String, SubProps>();

    static {
        init();
    }

    public static final String getBaseFolder() {
        return baseFolder;
    }

    public static final SubProps getSubProps(String name) {
        if (!subs.containsKey(name)) {
            throw new LucidPairsLoadException("not exist sub-props name " + name);
        }
        return subs.get(name);
    }

    private static void init() {
        List<String> lines = new ArrayList<String>();

        // find the prop file, read lines
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(LucidProps.class.getResource("/lucidpairs.properties").getFile()));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            throw new LucidPairsLoadException("error in load lucidpairs.properties: " + e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }

        // parse lines, key=value
        SubProps sub = null;
        for (String line : lines) {
            if (line.startsWith("[") && line.endsWith("]")) {
                String subName = line.substring(1, line.length() - 1);
                sub = new SubProps();
                subs.put(subName, sub);
            } else {
                String[] kv = line.split("\\s*=\\s*", 2);
                if (sub == null) {

                    // base folder
                    if (kv[0].equals(BASE_FOLDER)) {
                        baseFolder = kv[1];
                    }
                } else {

                    // read sub-props
                    if (kv[0].equals(PERSIST_TYPE)) {
                        sub.type = kv[1];
                    }
                }
            }
        }

    }

    public static class SubProps {

        private String type;

        private int period;

        public String getType() {
            return type;
        }

        public int getPeriod() {
            return period;
        }
    }
}
