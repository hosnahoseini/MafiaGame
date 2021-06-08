package org.HO.Client;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ReadThread implements Runnable {
    private Socket socket;
    private DataInputStream in;
    private Player player;
    private static final LoggingManager logger = new LoggingManager(ReadThread.class.getName());

    public ReadThread(Socket socket, Player player) {
        this.socket = socket;
        try {
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.player = player;

    }

    @Override
    public void run() {
        String message = "";
        String end = player.getName() + " left chat";

        do {
            try {

                message = in.readUTF();
                logger.log(player.getName() + " read " + message + " in chat", LogLevels.INFO);

                if (message.equalsIgnoreCase(end))
                    break;

                if (message.equals("Chat time ended")) {
                    System.out.println(message);
                    break;
                }

                if (message.equals(player.getName() + " exit")) {
                    System.out.println(player.readTxt());
                    Scanner scanner = new Scanner(System.in);
                    String result = scanner.next();
                    player.writeTxt(result);
                    if (result.equals("n")) {
                        player.close();
                        System.exit(5);
                        break;
                    }
                }

                System.out.println(message);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (true);
        System.out.println("END READ");
        Thread.currentThread().

                interrupt();

    }
}
