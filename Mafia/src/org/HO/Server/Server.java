package org.HO.Server;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;

import org.HO.PlayerRole;
import org.HO.SharedData;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private SharedData sharedData = SharedData.getInstance();
    private static final LoggingManager logger = new LoggingManager(Server.class.getName());

    public Server(int numberOfPlayers) {
        sharedData.numberOfPlayers = numberOfPlayers;
    }

    public void start(int port) {

        ExecutorService pool = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            for (int i = 0; i < sharedData.numberOfPlayers; i++) {
                Socket connection = serverSocket.accept();
                logger.log("Client [" + i + "] connected successfully", LogLevels.INFO);
                Thread thread = new Thread(new ClientHandler(connection));
                pool.execute(thread);
            }

            while (true) {
                if (sharedData.players.size() == sharedData.numberOfPlayers)
                    break;
            }

            introducing();
            sendMessageToAllClients("MORNING");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToAllClients(String msg) throws IOException {
        for (ClientHandler player: sharedData.players) {
            player.writeTxt(msg);
        }
    }

    public void introducing() {
        mafiaIntroducing();

    }

    public void mafiaIntroducing() {
        for (ClientHandler mafia : sharedData.getMafias()) {
                try {
                    logger.log("introducing mafias", LogLevels.INFO);
                    for (ClientHandler otherMafia : sharedData.getMafias()) {
                        if (!otherMafia.equals(mafia)) {
                            mafia.writeTxt(otherMafia.getName() + " is " + otherMafia.getRole());
                            logger.log(otherMafia.getName() + " is " + otherMafia.getRole(), LogLevels.INFO);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }
}
