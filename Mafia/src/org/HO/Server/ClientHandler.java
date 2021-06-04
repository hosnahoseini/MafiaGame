package org.HO.Server;

import org.HO.Initializer;
import org.HO.Player;
import org.HO.SharedData;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private SharedData sharedData;
    private Initializer initializer;
    private Player player;

    public ClientHandler(Socket connection) throws IOException {
        sharedData = SharedData.getInstance();
        initializer = Initializer.getInstance();
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


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setNameFromClient() throws IOException, ClassNotFoundException {
        String name;
        while (true) {
            name = (String) player.getInObj().readObject();
            if (sharedData.checkIfNameIsRepetitive(name))
                player.getOutObj().writeObject(false);
            else {
                player.getOutObj().writeObject(true);
                break;
            }
        }
        player.setName(name);
    }
}