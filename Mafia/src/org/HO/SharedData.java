package org.HO;


import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SharedData {
    private static SharedData instance;
    public BlockingQueue<Player> players;
    public int numberOfPlayers;
    public int numberOfNormalMafias;
    public int numberOfNormalPeople;
    public Player killed;
    public Player killedByMafias;
    public Player healedMafia;
    public Player healedCitizen;
    public Player killedByProfessional;
    public Player mute;
    public PlayerRole winner;
    public boolean killedInquired = false;
    public int numberOfInquiries = 0;
    public int numberOfKillDieHard = 0;
    public ArrayList<Player> killedPlayers;

    private SharedData() {
        killedPlayers = new ArrayList<>();
        players = new LinkedBlockingQueue<>();
//        numberOfNormalMafias = 1;
//        numberOfNormalPeople = 0;
        numberOfNormalMafias = (numberOfPlayers / 3) - 2;
        numberOfNormalPeople = numberOfPlayers - numberOfNormalMafias - 8;
    }

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public void addToSharedData(Player player) {
        players.add(player);

    }

    public ArrayList<Player> getMafias() {
        ArrayList<Player> mafias = new ArrayList<>();
        for (Player player : players)
            if (player.isMafia() && player.isAlive())
                mafias.add(player);
        return mafias;

    }

    public ArrayList<Player> getCitizens() {
        ArrayList<Player> citizens = new ArrayList<>();
        for (Player player : players)
            if (player.isCitizen() && player.isAlive())
                citizens.add(player);
        return citizens;
    }

    public Player getSingleRole(PlayerRole role) {
        for (Player player : players)
            if (player.getRole().equals(role))
                return player;
        return null;
    }

    public ArrayList<Player> getAlivePlayers() {
        ArrayList<Player> alives = new ArrayList<>();
        for (Player player : players)
            if (player.isAlive())
                alives.add(player);
        return alives;
    }

    public ArrayList<Player> getAbleToReadChats() {
        ArrayList<Player> ableToReads = new ArrayList<>();
        for (Player player : players)
            if (player.isAbleToReadChat())
                ableToReads.add(player);
        return ableToReads;
    }

    public boolean checkIfNameIsRepetitive(String name) {
        for (Player player : players)
            if (player.getName().equals(name))
                return true;
        return false;
    }

    public Player findPlayerWithName(String name) {
        for (Player player : players)
            if (player.getName().equals(name))
                return player;
        System.out.println("i cant find " + name);
        return null;
    }

    public String showKilledRoles() {
        String killedRoles = "";
        for (Player player : killedPlayers)
            killedRoles += player.getRole() + "\n";

        return killedRoles;
    }

    /**
     * reset data for new loop
     */
    public void reset() {
        killed = null;
        killedByMafias = null;
        healedMafia = null;
        healedCitizen = null;
        killedByProfessional = null;
        killedInquired = false;
        mute = null;
    }
}