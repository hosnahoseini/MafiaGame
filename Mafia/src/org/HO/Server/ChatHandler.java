package org.HO.Server;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;
import org.HO.SharedData;

import java.io.IOException;
import java.util.ArrayList;

public class ChatHandler implements Runnable {

    private Player player;
    private ArrayList<Player> writers;
    private ArrayList<Player> readers;
    private SharedData sharedData = SharedData.getInstance();
    private static final LoggingManager logger = new LoggingManager(ChatHandler.class.getName());

    public ChatHandler(Player player) {
        writers = sharedData.getAlivePlayers();
        readers = sharedData.getAbleToReadChats();
        this.player = player;
        logger.log("New player use chat handler", LogLevels.INFO);
    }

    @Override
    public void run() {
        String clientMessage;
        try {
            do {
                clientMessage = player.getIn().readUTF();
                String serverMessage = "[ " + player.getName() + " ]: " + clientMessage;

                if (clientMessage.equalsIgnoreCase("done")) {
                    serverMessage = player.getName() + " left chat";
                    broadcast(serverMessage);
                    writers.remove(player);
                    readers.remove(player);
                    break;

                }

                logger.log("server receives " + clientMessage, LogLevels.INFO);
                broadcast(serverMessage);
                logger.log("server broad cast " + clientMessage, LogLevels.INFO);
            } while (!checkIfChatEnded());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread.currentThread().interrupt();
    }

    public boolean checkIfChatEnded() {
        if (writers.size() == 0)
            return true;
        return false;
    }

    public void broadcast(String msg) {

        for (Player player : readers) {

            try {
                player.writeTxt(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
