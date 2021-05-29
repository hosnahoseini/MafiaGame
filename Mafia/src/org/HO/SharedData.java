package org.HO;

public class SharedData {
    private static SharedData instance;

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public static int numberOfPlayers;
}