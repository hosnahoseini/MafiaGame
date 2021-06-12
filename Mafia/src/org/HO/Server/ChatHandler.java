package org.HO.Server;

import org.HO.FileUtils;
import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;
import org.HO.SharedData;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * A class for handling chat
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class ChatHandler implements Runnable {

    private Player player;
    private ArrayList<Player> writers;
    private ArrayList<Player> readers;
    private SharedData sharedData = SharedData.getInstance();
    private static final LoggingManager logger = new LoggingManager(ChatHandler.class.getName());
    private FileUtils fileUtils = new FileUtils();

    public ChatHandler(Player player) {
        writers = sharedData.getAlivePlayers();
        readers = sharedData.getPlayers();
        this.player = player;
        logger.log("New player use chat handler", LogLevels.INFO);
    }

    @Override
    public void run() {
        String clientMessage;

        do {
            clientMessage = readWithExit(player);
            logger.log("server receives " + clientMessage, LogLevels.INFO);
            String serverMessage = "[ " + player.getName() + " ]: " + clientMessage;

            if (clientMessage.equalsIgnoreCase("done")) {
                serverMessage = player.getName() + " left chat";
                broadcast(serverMessage);
                writers.remove(player);
                readers.remove(player);
                break;

            }

            if (clientMessage.equals("exit")) {
                serverMessage = player.getName() + " exit";
                broadcast(serverMessage);
                writers.remove(player);
                readers.remove(player);
                removePlayer(player);
                break;
            }

            if(clientMessage.equals("end")) {
                broadcast("end");
                break;

            }

            if(clientMessage.equals("HISTORY"))
                previousChats(player);

            fileUtils.fileWriterByBuffer("chatBoxTemp.txt", serverMessage);
            logger.log("server receives " + clientMessage, LogLevels.INFO);
            broadcast(serverMessage);
            logger.log("server broad cast " + clientMessage, LogLevels.INFO);
        } while (true);

        System.out.println(player  + " chat  handler ended");
        Thread.currentThread().interrupt();
    }

    /**
     * broadcast message to players
     *
     * @param msg message
     */
    public void broadcast(String msg) {
        for (Player player : readers)
            player.writeTxt(msg);
    }

    /**
     * show previous chats to client
     *
     * @param player client
     */
    private void previousChats(Player player) {
        String chatBox = fileUtils.fileReaderByBuffer("chatBox.txt");
        chatBox += "\n----END!----\n";
        player.writeTxt(chatBox);
    }

    /**
     * remove player
     * @param killed player to be removed
     */
    private void removePlayer(Player killed) {
        sharedData.killedPlayers.add(killed);
        killed.setAlive(false);
        killed.writeTxt("Do you want to see rest of the game?(y/n)");
        String result = killed.readTxt();
        if (result.equals("n")) {
            sharedData.players.remove(killed);
            player.close();
            try {
                killed.getConnection().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * read message from client and check if he wants to exit or disconnected
     * @param player client
     * @return message
     */
    public String readWithExit(Player player) {
        String input = "";
        try {
            input = player.getIn().readUTF();
        } catch (SocketException e) {
            player.close();
            sharedData.players.remove(player);
            writers.remove(player);
            readers.remove(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

}
