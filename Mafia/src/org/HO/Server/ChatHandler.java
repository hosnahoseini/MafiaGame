package org.HO.Server;

import org.HO.FileUtils;
import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;
import org.HO.SharedData;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A class for handling chat
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class ChatHandler implements Runnable {

    private Player player;
    private ArrayList<Player> readers;
    private SharedData sharedData = SharedData.getInstance();
    private static final LoggingManager logger = new LoggingManager(ChatHandler.class.getName());
    private FileUtils fileUtils = new FileUtils();
    private ServerOutputHandling serverOutputHandling = new ServerOutputHandling();
    private boolean disconnect = false;

    public ChatHandler(Player player) {
        readers = sharedData.getPlayers();
        this.player = player;
        logger.log("New player use chat handler", LogLevels.INFO);
    }

    @Override
    public void run() {
        String clientMessage;

        do {
            readers = sharedData.getPlayers();
            clientMessage = player.readTxt();
            String serverMessage = "[ " + player.getName() + " ]: " + clientMessage;
            if (clientMessage != null) {
                if (clientMessage.equalsIgnoreCase("done")) {
                    serverMessage = player.getName() + " left chat";
                    broadcast(serverMessage);
                    readers.remove(player);
                    break;

                }

                if (clientMessage.equals("exit")) {
                    serverMessage = player.getName() + " exit";
                    broadcast(serverMessage);
                    readers.remove(player);
                    serverOutputHandling.removePlayer(player);
                    break;
                }

                if (clientMessage.equals("Chat time ended")) {
                    broadcast("Chat time ended");
                    break;

                }

                if (clientMessage.equals("HISTORY")) {
                    serverMessage = player.getName() + " request for chat HISTORY";
                    previousChats(player);
                }

                fileUtils.fileWriterByBuffer("chatBoxTemp.txt", serverMessage);
                logger.log("server " + player + " receives " + clientMessage, LogLevels.INFO);
                broadcast(serverMessage);
                if (disconnect)
                    break;
                logger.log("server broad cast " + clientMessage, LogLevels.INFO);
            }
        } while (true);

        Thread.currentThread().interrupt();
    }

    /**
     * broadcast message to players
     *
     * @param msg message
     */
    public void broadcast(String msg) {
        try {
            for (Player player : readers)
                player.writeTxt(msg);
        } catch (IOException e) {
            player.close();
            sharedData.players.remove(player);
            readers.remove(player);
            disconnect = true;
        }
    }

    /**
     * show previous chats to client
     *
     * @param player client
     */
    private void previousChats(Player player) {
        String chatBox = fileUtils.fileReaderByBuffer("chatBox.txt");
        chatBox += "\n----END!----\n";
        try {
            player.writeTxt(chatBox);
        } catch (IOException e) {
            player.close();
            sharedData.players.remove(player);
        }
    }
}
