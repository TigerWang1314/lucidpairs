package com.maygic.lucidpairs.persistor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileInteraction {

    private static final String FILE_LOCK = "lock";
    private static final String FILE_PAIRS = "pairs";
    private static final String FILE_TMP = "tmp";

    private String name;
    private String fileFolder;

    private File pairsFile;

    private File tmpFile;

    // lock
    private File lockFile;
    private RandomAccessFile accessFile;
    private FileLock fileLock;

    private List<String> lines = new ArrayList<String>();

    private String replace$N = "[[SELF-PAIRS_\\N]]";
    private String replace$R = "[[SELF-PAIRS_\\R]]";

    private String fileEnd = "[[SELF-PAIRS_ENDING]]";

    public FileInteraction(String baseFolder, String name) throws IOException {
        this.name = name;
        this.fileFolder = baseFolder + File.separator + name;
        init();
    }

    private void init() throws IOException {

        // create folder
        File folder = new File(fileFolder);
        if (!folder.exists()) {
            folder.mkdirs(); // create folder
        } else if (!folder.isDirectory()) {
            throw new IOException(folder.getAbsolutePath() + " is not a folder");
        }

        // lock file for this process
        // create lock file
        lockFile = createFile(FILE_LOCK);
        accessFile = new RandomAccessFile(lockFile.getAbsolutePath(), "rw");
        fileLock = accessFile.getChannel().tryLock();
        if (fileLock == null) {
            throw new IOException("can not lock file");
        }

        // create pairs file
        pairsFile = createFile(FILE_PAIRS);

        // create tmp file
        tmpFile = createFile(FILE_TMP);
    }

    private File createFile(String name) throws IOException {
        File file = new File(fileFolder + File.separator + name);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public void persist(Map<String, String> pairs) {

        // to lines
        for (Map.Entry<String, String> entry : pairs.entrySet()) {
            lines.add(entry.getKey() + "=" + transfer(entry.getValue()));
        }

        // write lines to tmp-file
        try {
            writeFile(tmpFile, true);
            writeFile(pairsFile, false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        lines.clear();
    }

    private void writeFile(File file, boolean addEnding) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        try {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }

            // add ending
            if (addEnding) {
                bw.write(fileEnd);
            }

            bw.flush();
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }

    private String transfer(String value) {
        return value.replace("\n", replace$N).replace("\r", replace$R);
    }

    private String untransfer(String value) {
        return value.replace(replace$N, "\n").replace(replace$R, "\r");
    }

    public void load(Map<String, String> pairs) {
        List<String> readLines = new ArrayList<String>();

        // read from tmp
        try {
            if (!readFile(tmpFile, readLines)) {
                readFile(pairsFile, readLines);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // parse pairs
        for (String line : readLines) {
            String[] ls = line.split("=", 2);
            String name = ls[0];
            String value = untransfer(ls[1]);
            pairs.put(name, value);
        }
    }

    private boolean readFile(File file, List<String> readLines) throws Exception {
        boolean findEnd = false;
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            String line = null;
            while ((line = br.readLine()) != null) {

                if (line.startsWith("\\s*#")) {
                    continue;
                }

                if (fileEnd.equals(line)) {
                    findEnd = true;
                    break;
                }

                if (!line.contains("=")) {
                    continue;
                }

                readLines.add(line);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }

        return findEnd;
    }

    public void release() {
        try {
            fileLock.release();
        } catch (IOException e) {
        }
        try {
            accessFile.close();
        } catch (IOException e) {
        }
    }
}
