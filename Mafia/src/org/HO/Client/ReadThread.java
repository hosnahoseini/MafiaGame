package org.HO.Client;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReadThread implements Runnable{
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
        logger.log(String.valueOf(socket), LogLevels.INFO);

        logger.log("startd read therad", LogLevels.INFO);
        logger.log(String.valueOf(this.socket.isClosed()), LogLevels.ERROR);
    }

    @Override
    public void run() {
        String message = "";
        String end = player.getName() + " left chat";

        do{
            try {

                logger.log(String.valueOf(socket.isClosed())+ "2" + player.getName(), LogLevels.ERROR);
                message = in.readUTF();
                logger.log(player.getName() + " read " + message +" in chat", LogLevels.INFO);

                if(message.equalsIgnoreCase(end) || message.equals("Chat time ended")) {
                    break;
                }
                System.out.println(message);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }while (true);
        System.out.println("END READ");
        Thread.currentThread().interrupt();

    }
}
