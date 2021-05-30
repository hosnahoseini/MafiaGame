package org.HO.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggingManager {
    private final String ROOT_DIR = "./log/";
    private final String LOG_FILE = "log.txt";
    private final String ERROR_FILE = "./log/error/";
    private final String CLASS_NAME;

    public LoggingManager(String CLASS_NAME) {
        this.CLASS_NAME = CLASS_NAME;
    }
    public void log(String msg, LogLevels level) {
        try {
            File file = new File("log.txt");
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write(logPattern(msg, level.toString()));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String logPattern(String msg, String level) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String result = formatter.format(date) + " [" + level + "] " + CLASS_NAME
                + " ---> " + msg + "\n";
        return result;
    }
}
