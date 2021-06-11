package org.HO;


import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * a class for shared and main data of game(singleton design pattern)
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
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

    /**
     * constructor
     */
    private SharedData() {
        killedPlayers = new ArrayList<>();
        players = new LinkedBlockingQueue<>();
//        numberOfNormalMafias = 1;
//        numberOfNormalPeople = 0;
        numberOfNormalMafias = (numberOfPlayers / 3) - 2;
        numberOfNormalPeople = numberOfPlayers - numberOfNormalMafias - 8;
    }

    /**
     * get instance of shared data
     * @return shared data
     */
    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    /**
     * add a player to players
     * @param player new player
     */
    public void addToSharedData(Player player) {
        players.add(player);

    }

    /**
     * get players who are alive mafias
     * @return alive mafias(normal-god father-dr lecter)
     */
    public ArrayList<Player> getMafias() {
        ArrayList<Player> mafias = new ArrayList<>();
        for (Player player : players)
            if (player.isMafia() && player.isAlive())
                mafias.add(player);
        return mafias;

    }

    /**
     * get all the players
     * @return players
     */
    public ArrayList<Player> getPlayers() {
        ArrayList<Player> playerArrayList = new ArrayList<>();
        for (Player player : players)
                playerArrayList.add(player);
        return playerArrayList;

    }

    /**
     * get players who are alive citizens
     * @return alive citizen
     */
    public ArrayList<Player> getCitizens() {
        ArrayList<Player> citizens = new ArrayList<>();
        for (Player player : players)
            if (player.isCitizen() && player.isAlive())
                citizens.add(player);
        return citizens;
    }

    /**
     * get a player with specific role
     * @param role role
     * @return player with the role
     */
    public Player getSingleRole(PlayerRole role) {
        for (Player player : players)
            if (player.getRole().equals(role))
                return player;
        return null;
    }

    /**
     * get alive players
     * @return alive players
     */
    public ArrayList<Player> getAlivePlayers() {
        ArrayList<Player> alives = new ArrayList<>();
        for (Player player : players)
            if (player.isAlive())
                alives.add(player);
        return alives;
    }

    /**
     * check if a nme has already taken or not
     * @param name name
     * @return true if s.o. got this name before
     */
    public boolean checkIfNameIsRepetitive(String name) {
        for (Player player : players)
            if (player.getName().equals(name))
                return true;
        return false;
    }

    /**
     * find a player with his name
     * @param name name
     * @return player with the name or null if there isn't such player
     */
    public Player findPlayerWithName(String name) {
        for (Player player : players)
            if (player.getName().equals(name))
                return player;
        System.out.println("i cant find " + name);
        return null;
    }

    /**
     * show roles whose players have been killed
     * @return string contains all the dead roles
     */
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