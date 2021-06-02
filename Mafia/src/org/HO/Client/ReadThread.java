package org.HO.Client;

import org.HO.Initializer;
import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Server.ClientHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReadThread implements Runnable{
    private Socket socket;
    private DataInputStream in;
    private String name;
    private static final LoggingManager logger = new LoggingManager(ReadThread.class.getName());

    public ReadThread(Socket socket, String name) {
        this.socket = socket;
        try {
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.name = name;
        logger.log(String.valueOf(socket), LogLevels.INFO);

        logger.log("startd read therad", LogLevels.INFO);
        logger.log(String.valueOf(this.socket.isClosed()), LogLevels.ERROR);
    }

    @Override
    public void run() {
        String message = "";
        do{
            try {

                logger.log(String.valueOf(socket.isClosed())+ "2" + name, LogLevels.ERROR);
                message = in.readUTF();
                logger.log(name + " read " + message +" in chat", LogLevels.INFO);
                System.out.println(message + "\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }while (!message.equalsIgnoreCase("done"));
    }
}
