package org.HO.Client;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Server.ClientHandler;
import org.HO.Server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class WriteThread implements Runnable{
    private String name;
    public Socket socket;
    private DataOutputStream out;
    private static final LoggingManager logger = new LoggingManager(WriteThread.class.getName());

    public WriteThread(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
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
            logger.log(String.valueOf(socket.isClosed())+ "2" + name, LogLevels.ERROR);
            logger.log(name + " wants to write " + message +" in chat", LogLevels.INFO);
            try {
                out.writeUTF("[ " + name + " ]: " + message);
                logger.log(name + " write " + message +" in chat", LogLevels.INFO);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } while (!message.equalsIgnoreCase("done"));


    }

}
