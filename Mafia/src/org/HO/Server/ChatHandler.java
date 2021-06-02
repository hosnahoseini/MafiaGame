package org.HO.Server;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.SharedData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ChatHandler implements Runnable {

    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private ClientHandler player;
    private BlockingQueue<ClientHandler> chatters;
    private static final LoggingManager logger = new LoggingManager(ChatHandler.class.getName());

    public ChatHandler(ClientHandler player, Server server, BlockingQueue<ClientHandler> chatters) {
        this.chatters = chatters;
        this.player = player;
        this.server = server;
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
                String serverMessage = "[ " + player.getName() + " ]: " + clientMessage;

                if (clientMessage.equalsIgnoreCase("done")) {
                    chatters.remove(player);
                    serverMessage = player.getName() + " left chat";
                }

                logger.log("server receives " + clientMessage, LogLevels.INFO);
                broadcast(serverMessage);
                logger.log("server broad cast " + clientMessage, LogLevels.INFO);
            } while (!checkIfChatEnded());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfChatEnded() {
        if (chatters.size() == 0)
            return true;
        return false;
    }

    public void broadcast(String msg) {

        for (ClientHandler player : chatters) {

            try {
                player.writeTxt(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
