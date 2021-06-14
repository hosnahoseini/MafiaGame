package org.HO;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
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
    public void writeTxt (String text) throws SocketException{
        try {
            this.out.writeUTF(text);
        } catch(IOException e) {
            System.err.println ("Some went Wrong in I/O in player " + name);
            e.printStackTrace();
            //TODO
        }
    }

    /**
     * player write a text in socket buffer from client side
     * @param text text
     */
    public void writeTxtClient (String text){
        try {
            this.out.writeUTF(text);
        } catch(IOException e) {
            System.err.println ("Some went Wrong in I/O in player " + name);
            e.printStackTrace();
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
            e.printStackTrace();
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

    /**
     * Gets connection.
     *
     * @return the connection
     */
    public Socket getConnection() {
        return connection;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets role.
     *
     * @return the role
     */
    public PlayerRole getRole() {
        return role;
    }

    /**
     * Is alive boolean.
     *
     * @return the boolean
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Sets alive.
     *
     * @param alive the alive
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Gets in.
     *
     * @return the in
     */
    public DataInputStream getIn() {
        return in;
    }

    /**
     * Gets out.
     *
     * @return the out
     */
    public DataOutputStream getOut() {
        return out;
    }

    /**
     * Gets out obj.
     *
     * @return the out obj
     */
    public ObjectOutputStream getOutObj() {
        return outObj;
    }

    /**
     * Gets in obj.
     *
     * @return the in obj
     */
    public ObjectInputStream getInObj() {
        return inObj;
    }

    /**
     * Is ready to play boolean.
     *
     * @return the boolean
     */
    public boolean isReadyToPlay() {
        return readyToPlay;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets role.
     *
     * @param role the role
     */
    public void setRole(PlayerRole role) {
        this.role = role;
    }

    /**
     * Sets ready to play.
     *
     * @param readyToPlay the ready to play
     */
    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    /**
     * Sets in.
     *
     * @param in the in
     */
    public void setIn(DataInputStream in) {
        this.in = in;
    }

    /**
     * Sets out.
     *
     * @param out the out
     */
    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    /**
     * Gets heal.
     *
     * @return the heal
     */
    public int getHeal() {
        return heal;
    }

    /**
     * Is mute boolean.
     *
     * @return the boolean
     */
    public boolean isMute() {
        return mute;
    }

    /**
     * Sets mute.
     *
     * @param mute the mute
     */
    public void setMute(boolean mute) {
        this.mute = mute;
    }

}
