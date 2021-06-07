package org.HO.Server;

import org.HO.FileUtils;
import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;
import org.HO.SharedData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class ChatHandler implements Runnable {

    private Player player;
    private ArrayList<Player> writers;
    private BlockingQueue<Player> readers;
    private SharedData sharedData = SharedData.getInstance();
    private static final LoggingManager logger = new LoggingManager(ChatHandler.class.getName());
    private boolean running = true;
    private FileUtils fileUtils = new FileUtils();

    public ChatHandler(Player player) {
        logger.log("New player use chat handler", LogLevels.INFO);
        writers = sharedData.getAlivePlayers();
        readers = sharedData.players;
        this.player = player;
    }

    @Override
    public void run() {
        String clientMessage;
        try {
            logger.log("send pc q" , LogLevels.INFO);

            if(player.readTxt().equals("y"))
                previousChats(player);
            do {
                clientMessage = player.getIn().readUTF();
                String serverMessage = "[ " + player.getName() + " ]: " + clientMessage;
                fileUtils.fileWriterByBuffer("chatBox.txt", serverMessage);
                if (clientMessage.equalsIgnoreCase("done")) {
                    serverMessage = player.getName() + " left chat";
                    broadcast(serverMessage);
                    writers.remove(player);
                    readers.remove(player);
                    running = false;
                    break;

                }

                logger.log("server receives " + clientMessage, LogLevels.INFO);
                broadcast(serverMessage);
                logger.log("server broad cast " + clientMessage, LogLevels.INFO);
            } while (!checkIfChatEnded() && running);
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
        for (Player player : readers)
            player.writeTxt(msg);
    }

    private void previousChats(Player player) {
        String chatBox = fileUtils.fileReaderByBuffer("chatBox.txt");
        player.writeTxt(chatBox);
    }

}
