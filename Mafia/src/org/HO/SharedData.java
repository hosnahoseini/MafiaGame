package org.HO;


import org.HO.Server.ClientHandler;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class SharedData {
    private static SharedData instance;
    public BlockingQueue<ClientHandler> players;
    public ConcurrentHashMap<PlayerRole,ArrayList<ClientHandler>> playerWithRoles;
    public int numberOfPlayers;
    public int numberOfNormalMafias;
    public int numberOfNormalPeople;


    private SharedData(){
        players = new LinkedBlockingQueue<>();
        numberOfNormalMafias = 2;
        numberOfNormalPeople = 1;
//        int numberOfMafia = (numberOfPlayers / 3) - 2;
//        int numberOfPeople = numberOfPlayers - numberOfMafia - 7;
    }

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public void addToSharedData(ClientHandler player) {
        try {
            players.put(player);
            //playerWithRoles.get(player.getRole()).add(player);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ClientHandler> getMafias(){
        ArrayList<ClientHandler> mafias = new ArrayList<>();
        for(ClientHandler player : players)
            if(player.isMafia())
                mafias.add(player);
            return mafias;

    }

    public ClientHandler getSingleRole(PlayerRole role){
        for(ClientHandler player : players)
            if(player.getRole().equals(role))
                return player;
            return null;
    }

    public boolean checkIfNameIsRepetitive(String name){
        for(ClientHandler player : players)
            if(player.getName().equals(name))
                return true;
            return false;
    }
}