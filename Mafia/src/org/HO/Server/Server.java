package org.HO.Server;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;

import org.HO.PlayerRole;
import org.HO.SharedData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
                if (checkIfEveryOneISReady())
                    break;
            }

            introducing();
            sendMessageToAllClients("MORNING");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfEveryOneISReady() {
        for (ClientHandler player : sharedData.players) {
            if (!player.isReadyToPlay())
                return false;
        }
        return true;
    }

    private void sendMessageToAllClients(String msg) throws IOException {
        for (ClientHandler player : sharedData.players) {
            player.writeTxt(msg);
        }
    }

    public void introducing() {
        try {
            introduceMafias();
            introduceDrCityToMayor();
        } catch (IOException e) {
            logger.log("cant introduce", LogLevels.ERROR);
        }

    }

    public void introduceMafias() throws IOException {
        for (ClientHandler mafia : sharedData.getMafias()) {

            logger.log("introducing mafias", LogLevels.INFO);
            for (ClientHandler otherMafia : sharedData.getMafias()) {
                if (!otherMafia.equals(mafia)) {
                    mafia.writeTxt(otherMafia.getName() + " is " + otherMafia.getRole());
                    logger.log(otherMafia.getName() + " is " + otherMafia.getRole(), LogLevels.INFO);
                }
            }

        }
    }

    public void introduceDrCityToMayor() throws IOException {
        String msg = sharedData.getSingleRole(PlayerRole.DR_CITY).getName() + " is doctor of city";
        sharedData.getSingleRole(PlayerRole.MAYOR).writeTxt(msg);
    }
}
