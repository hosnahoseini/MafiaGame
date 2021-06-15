package org.HO.Client;

import org.HO.Color;
import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;

import java.util.Scanner;

/**
 * A class to handle a thread to reading chat in client side
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */

public class ReadThread implements Runnable {
    private Player player;
    private static final LoggingManager logger = new LoggingManager(ReadThread.class.getName());
    private ClientInputHandling clientInputHandling = new ClientInputHandling();
    public ReadThread(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        String message = "";
        String end = player.getName() + " left chat";
        do {
            message = player.readTxt();
            if(message != null) {
                logger.log(player.getName() + " read " + message + " in chat", LogLevels.INFO);

                if (message.equalsIgnoreCase(end) || message.equals("Chat time ended")) break;


                if (message.equals(player.getName() + " exit")) {
                    clientInputHandling.removePlayer(player);
                    break;
                }

                System.out.println(Color.BLUE + message + Color.RESET);
            }
        } while (true);

    }

}
