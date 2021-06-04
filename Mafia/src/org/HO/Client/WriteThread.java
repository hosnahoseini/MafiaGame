package org.HO.Client;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class WriteThread implements Runnable{
    private Player player;
    public Socket socket;
    private DataOutputStream out;
    private static final LoggingManager logger = new LoggingManager(WriteThread.class.getName());

    public WriteThread(Socket socket, Player player) {
        this.socket = socket;
        this.player = player;
        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.log(String.valueOf(socket.isClosed()), LogLevels.ERROR);
        logger.log("startd write therad", LogLevels.INFO);

    }


    @Override
    public void run() {

        Scanner scanner = new Scanner(System.in);
        String message;

        do {
            message = scanner.nextLine();
            logger.log(String.valueOf(socket.isClosed())+ "2" + player.getName(), LogLevels.ERROR);
            logger.log(player.getName() + " wants to write " + message +" in chat", LogLevels.INFO);
            try {
                out.writeUTF(message);
                logger.log(player.getName() + " write " + message +" in chat", LogLevels.INFO);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } while (!message.equalsIgnoreCase("done"));

        System.out.println("END WRITE");
    }

}
