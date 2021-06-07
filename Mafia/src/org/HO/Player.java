package org.HO;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

import static org.HO.PlayerRole.*;
import static org.HO.PlayerRole.MAYOR;

public class Player implements Serializable{

    private transient Socket connection;
    private String name;
    private PlayerRole role;
    private boolean alive = true;
    private boolean readyToPlay = false;
    private boolean ableToWriteChat = true;
    private int heal = 0;
    private transient DataInputStream in;
    private transient DataOutputStream out;
    private transient ObjectOutputStream outObj;
    private transient ObjectInputStream inObj ;

    public Player(Socket connection) throws IOException {
        this.connection = connection;
        in = new DataInputStream(connection.getInputStream());
        out = new DataOutputStream(connection.getOutputStream());
        outObj = new ObjectOutputStream(out);
        inObj = new ObjectInputStream(in);
    }

    public boolean isMafia(){
        if(role == NORMAL_MAFIA || role ==DR_LECTER || role == GOD_FATHER)
            return true;
        return false;
    }

    public boolean isCitizen(){
        if(role == NORMAL_PEOPLE || role == DETECTIVE || role == PROFESSIONAL ||
                role == PSYCHOLOGIST || role == DIE_HARD || role == MAYOR || role == DR_CITY)
            return true;
        return false;
    }

    public boolean isNormalMafia(){
        if(role == PlayerRole.NORMAL_MAFIA)
            return true;
        return false;
    }

    public void writeTxt(String text) {
        try {
            this.out.writeUTF(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readTxt() {
        try {
            return this.in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeObj(Object obj) {
        try {
            this.outObj.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object readObj() {
        try {
            return this.inObj.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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

    public void setConnection(Socket connection) {
        this.connection = connection;
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

    public void setOutObj(ObjectOutputStream outObj) {
        this.outObj = outObj;
    }

    public void setInObj(ObjectInputStream inObj) {
        this.inObj = inObj;
    }

    public int getHeal() {
        return heal;
    }

    public boolean isAbleToWriteChat() {
        return ableToWriteChat;
    }

    public void setAbleToWriteChat(boolean ableToWriteChat) {
        this.ableToWriteChat = ableToWriteChat;
    }
}
