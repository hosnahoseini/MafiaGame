package org.HO.Server;

import org.HO.Initializer;
import org.HO.Player;
import org.HO.SharedData;

import java.io.*;
import java.net.Socket;

/**
 * A class for handling acceptance of client s
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class ClientHandler implements Runnable {

    private SharedData sharedData;
    private Initializer initializer;
    private Player player;

    public ClientHandler(Socket connection){
        sharedData = SharedData.getInstance();
        initializer = Initializer.getInstance(sharedData.numberOfPlayers);
        player = new Player(connection);
    }

    @Override
    public void run() {
        try {

            player.setRole(initializer.assignRole());
            setNameFromClient();
            player.getOutObj().writeObject(player.getRole());
            sharedData.addToSharedData(player);
            player.setReadyToPlay((boolean) player.getInObj().readObject());


        } catch (IOException e) {
            System.err.println("Can't read role");
        } catch (ClassNotFoundException e) {
            System.err.println("Can't convert to boolean");
        }
    }

    /**
     * get name from client and set it in server data
     */
    private void setNameFromClient(){
        String name;
        while (true) {
            try {
                name =player.readTxt();
                if (sharedData.checkIfNameIsRepetitive(name))
                    player.getOutObj().writeObject(false);
                else {
                    player.getOutObj().writeObject(true);
                    break;
                }
            } catch (IOException e) {
                System.err.println("Can't read name");
            }
        }
        player.setName(name);
    }
}