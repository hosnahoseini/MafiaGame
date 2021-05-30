package org.HO;

import org.HO.Client.Client;
import org.HO.Server.ClientHandler;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class SharedData {
    private static SharedData instance;
    public ConcurrentHashMap<PlayerRole, ArrayList<ClientHandler>> players;
    public int numberOfPlayers;

    private SharedData(){
        players = new ConcurrentHashMap<>();
    }

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

}