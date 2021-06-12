package org.HO.Client;

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

    public ReadThread(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        String message = "";
        String end = player.getName() + " left chat";

        do {
            message = player.readTxt();
            logger.log(player.getName() + " read " + message + " in chat", LogLevels.INFO);

            if (message.equalsIgnoreCase(end) || message.equals("Chat time ended") || message.equals("end")) break;

//            if (chatTimeEndedHandler(message)) break;

            if (exitMessageHandler(message)) break;

            System.out.println(message);

        } while (true);
        System.out.println("END READ");
//        Thread.currentThread().interrupt();

    }

    private boolean chatTimeEndedHandler(String message) {
        if (message.equals("Chat time ended")) {
            System.out.println(message);
            return true;
        }
        return false;
    }

    private boolean exitMessageHandler(String message) {
        if (message.equals(player.getName() + " exit")) {
            System.out.println(player.readTxt());
            Scanner scanner = new Scanner(System.in);
            String result = scanner.next();
            player.writeTxt(result);
            if (result.equals("n")) {
                player.close();
                System.exit(5);
                return true;
            }
        }
        return false;
    }
}
