package org.HO.Server;

import org.HO.Initializer;
import org.HO.PlayerRole;
import org.HO.SharedData;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

import static org.HO.PlayerRole.*;
import static org.HO.PlayerRole.MAYOR;

public class ClientHandler implements Runnable {

    private Socket connection;
    private SharedData sharedData;
    private Initializer initializer;
    private String name;
    private PlayerRole role;
    private boolean alive = true;
    private boolean readyToPlay = false;
    private boolean ableToReadChat = true;
    private DataInputStream in;
    private DataOutputStream out;
    private ObjectOutputStream outObj;
    private ObjectInputStream inObj ;

    public ClientHandler(Socket connection) throws IOException {
        this.connection = connection;
        sharedData = SharedData.getInstance();
        initializer = Initializer.getInstance();
        in = new DataInputStream(connection.getInputStream());
        out = new DataOutputStream(connection.getOutputStream());
        outObj = new ObjectOutputStream(out);
        inObj = new ObjectInputStream(in);
    }

    @Override
    public void run() {
        try {

            role = initializer.assignRole();
            setNameFromClient();
            outObj.writeObject(role);
            sharedData.addToSharedData(this);
            readyToPlay = (boolean) inObj.readObject();


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setNameFromClient() throws IOException, ClassNotFoundException {
        while (true){
            name = (String) inObj.readObject();
            if(sharedData.checkIfNameIsRepetitive(name))
                outObj.writeObject(false);
            else{
                outObj.writeObject(true);
                break;
            }
        }
    }

    public boolean isMafia(){
        if(role == NORMAL_MAFIA || role ==DR_LECTER || role == GOD_FATHER)
            return true;
        return false;
    }

    public boolean isCitizen(){
        if(role == NORMAL_PEOPLE || role == DETECTIVE || role == PROFESSIONAL ||
                role == PSYCHOLOGIST || role == DIE_HARD || role == MAYOR)
            return true;
        return false;
    }

    public boolean isNormalMafia(){
        if(role == PlayerRole.NORMAL_MAFIA)
            return true;
        return false;
    }

    public void writeTxt(String text) throws IOException {
        this.out.writeUTF(text);
    }
    public void writeObj(Object obj) throws IOException {
        this.outObj.writeObject(obj);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return alive == that.alive &&
                Objects.equals(connection, that.connection) &&
                Objects.equals(name, that.name) &&
                role == that.role;
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

    public boolean isAbleToReadChat() {
        return ableToReadChat;
    }
}
