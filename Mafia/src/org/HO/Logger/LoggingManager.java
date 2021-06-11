package org.HO.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class for logger
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class LoggingManager {

    private final String CLASS_NAME;

    public LoggingManager(String CLASS_NAME) {
        this.CLASS_NAME = CLASS_NAME;
    }

    /**
     * make a new log
     * @param msg message of log
     * @param level level of log
     */
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

    /**
     * put log in special format
     * @param msg message of log
     * @param level level of log
     * @return formatted log
     */
    private String logPattern(String msg, String level) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String result = formatter.format(date) + " [" + level + "] " + CLASS_NAME
                + " ---> " + msg + "\n";
        return result;
    }
}
