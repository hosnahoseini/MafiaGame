package org.HO.Server;

import org.HO.Initializer;
import org.HO.Logger.LogLevels;
import org.HO.Player;
import org.HO.PlayerRole;
import org.HO.SharedData;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private Socket connection;
    private SharedData sharedData;
    private Initializer initializer;
    private String name;
    private PlayerRole role;
    private boolean alive = true;
    DataInputStream in;
    DataOutputStream out;
    ObjectOutputStream outObj;
    ObjectInputStream inObj ;

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
            name = (String) inObj.readObject();
            sharedData.players.get(role).add(this);
            outObj.writeObject(role);
//            if (role == PlayerRole.NORMAL_MAFIA || role == PlayerRole.GOD_FATHER || role == PlayerRole.DR_LECTER)
//                mafiaIntroducing();


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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



}
