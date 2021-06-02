package org.HO.Server;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.SharedData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChatHandler implements Runnable {

    private SharedData sharedData;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private ClientHandler player;
    private static int numberOfPlayerEndedChat;
    private static final LoggingManager logger = new LoggingManager(ChatHandler.class.getName());

    public ChatHandler(ClientHandler player, Server server) {
        this.player = player;
        sharedData = SharedData.getInstance();
        this.server = server;
        numberOfPlayerEndedChat = 0;
        logger.log("New player use chat handler", LogLevels.INFO);
        try {
            in = new DataInputStream(player.getConnection().getInputStream());
            out = new DataOutputStream(player.getConnection().getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String clientMessage;
        try {
            do {
                clientMessage = in.readUTF();
                if(clientMessage.equalsIgnoreCase("done"))
                    numberOfPlayerEndedChat ++;
                logger.log("server receives " + clientMessage, LogLevels.INFO);
                server.sendMessageToAllClients(clientMessage);
                logger.log("server broad cast " + clientMessage, LogLevels.INFO);
            } while (checkIfChatEnded());
            numberOfPlayerEndedChat = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfChatEnded(){
        if(numberOfPlayerEndedChat == sharedData.numberOfPlayers)
            return true;
        return false;
    }

}
