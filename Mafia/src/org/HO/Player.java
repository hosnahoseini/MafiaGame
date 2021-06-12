package org.HO;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

import static org.HO.PlayerRole.*;
import static org.HO.PlayerRole.MAYOR;

/**
 * A class to hold player info
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class Player implements Serializable{

    private transient Socket connection;
    private String name;
    private PlayerRole role;
    private boolean alive = true;
    private boolean readyToPlay = false;
    private boolean mute = false;
    private int heal = 0;
    private transient DataInputStream in;
    private transient DataOutputStream out;
    private transient ObjectOutputStream outObj;
    private transient ObjectInputStream inObj ;

    public Player(Socket connection){
        this.connection = connection;
        try {
            in = new DataInputStream(connection.getInputStream());
            out = new DataOutputStream(connection.getOutputStream());
            outObj = new ObjectOutputStream(out);
            inObj = new ObjectInputStream(in);
        } catch (IOException e) {
            System.err.println ("Some went Wrong in I/O for client " + name);
        }
    }

    /**
     * check if the player is mafia
     * @return true if player is mafia
     */
    public boolean isMafia(){
        if(role == NORMAL_MAFIA || role ==DR_LECTER || role == GOD_FATHER)
            return true;
        return false;
    }

    /**
     * check if the player is citizen
     * @return true if player is citizen
     */
    public boolean isCitizen(){
        if(role == NORMAL_PEOPLE || role == DETECTIVE || role == PROFESSIONAL ||
                role == PSYCHOLOGIST || role == DIE_HARD || role == MAYOR || role == DR_CITY)
            return true;
        return false;
    }

    /**
     * player write a text in socket buffer
     * @param text text
     */
    public void writeTxt(String text) {
        try {
            this.out.writeUTF(text);
        } catch (IOException e) {
            System.err.println ("Some went Wrong in I/O in player " + name);
            //TODO
        }
    }

    /**
     * player read a text in socket buffer
     * @return text
     */
    public String readTxt() {
        try {
            return in.readUTF();
        }catch (IOException e) {
            System.err.println ("Some went Wrong in I/O in player " + name);
            //TODO
        }
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player that = (Player) o;
        return alive == that.alive &&
                Objects.equals(connection, that.connection) &&
                Objects.equals(name, that.name) &&
                role == that.role;
    }

    @Override
    public String toString(){
        return name;
    }

    /**
     * close a player and its socket and streams
     */
    public void close(){
        try {
            in.close();
            out.close();
            inObj.close();
            outObj.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * add to number of times player healed
     */
    public void heal(){
        heal ++;
    }

    public Socket getConnection() {
        return connection;
    }

    public String getName() {
        return name;
    }

    public PlayerRole getRole() {
        return role;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public ObjectOutputStream getOutObj() {
        return outObj;
    }

    public ObjectInputStream getInObj() {
        return inObj;
    }

    public boolean isReadyToPlay() {
        return readyToPlay;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }

    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    public void setIn(DataInputStream in) {
        this.in = in;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public int getHeal() {
        return heal;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    /**
     * get input from client and check exit
     * @param player client
     * @return input
     */
    public String writeWithExit(Player player){
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        if(input.equals("exit")) {
            player.writeTxt("exit");
            removePlayer(player);
        }
        return input;
    }

    /**
     * remove player in client side
     * @param player player
     */
    private void removePlayer(Player player) {
        player.readTxt();
        System.out.println(player.readTxt());
        Scanner scanner = new Scanner(System.in);
        String result = scanner.next();
        player.writeTxt(result);
        if (result.equals("n")) {
            player.close();
            System.exit(5);
        }else {
            while (true) {
                System.out.println(player.readTxt());
            }
        }
    }
}
