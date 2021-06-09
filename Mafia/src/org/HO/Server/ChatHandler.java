package org.HO.Server;

import org.HO.FileUtils;
import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;
import org.HO.SharedData;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

public class ChatHandler implements Runnable {

    private Player player;
    private ArrayList<Player> writers;
    private ArrayList<Player> readers;
    private SharedData sharedData = SharedData.getInstance();
    private static final LoggingManager logger = new LoggingManager(ChatHandler.class.getName());
    private boolean running = true;
    private FileUtils fileUtils = new FileUtils();

    public ChatHandler(Player player) {
        writers = sharedData.getAlivePlayers();
        readers = sharedData.getAbleToReadChats();
        this.player = player;
        logger.log("New player use chat handler", LogLevels.INFO);
    }

    @Override
    public void run() {
        String clientMessage;

        if (player.readTxt().equals("y"))
            previousChats(player);
            do {
                clientMessage = readWithExit(player);
                if (clientMessage == null)
                    break;
                String serverMessage = "[ " + player.getName() + " ]: " + clientMessage;
                fileUtils.fileWriterByBuffer("chatBoxTemp.txt", serverMessage);

                if (clientMessage.equalsIgnoreCase("done")) {
                    serverMessage = player.getName() + " left chat";
                    broadcast(serverMessage);
                    writers.remove(player);
                    readers.remove(player);
                    running = false;
                    sharedData.numberOfPlayerEndChat ++;
                    break;

                }

                if(clientMessage.equals("exit")) {
                    serverMessage = player.getName() + " exit";
                    broadcast(serverMessage);
                    writers.remove(player);
                    readers.remove(player);
                    removePlayer(player);
                    break;
                }

                logger.log("server receives " + clientMessage, LogLevels.INFO);
                broadcast(serverMessage);


                logger.log("server broad cast " + clientMessage, LogLevels.INFO);
            } while (!checkIfChatEnded() && running);

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

    private void removePlayer(Player killed) {
        sharedData.killedPlayers.add(killed);
        killed.setAlive(false);
        killed.writeTxt("Do you want to see rest of the game?(y/n)");
        String result = killed.readTxt();
        if (result.equals("n")) {
            killed.setAbleToReadChat(false);
            sharedData.players.remove(killed);
            try {
                killed.getConnection().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String readWithExit(Player player) {
        String input = "";
        try {
            input = player.getIn().readUTF();
        } catch (SocketException e){
            player.close();
            sharedData.players.remove(player);
            writers.remove(player);
            readers.remove(player);
            System.exit(7);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }
    private void previousChats(Player player) {
        String chatBox = fileUtils.fileReaderByBuffer("chatBox.txt");
        player.writeTxt(chatBox);
    }
}
