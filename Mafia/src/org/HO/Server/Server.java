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
    ExecutorService pool = Executors.newCachedThreadPool();

    public Server(int numberOfPlayers) {
        sharedData.numberOfPlayers = numberOfPlayers;
    }

    public void start(int port) {

        acceptClients(port);


        while (true) {
            if (checkIfEveryOneISReady())
                break;
        }

        introducing();
        sendMessageToAllClients("MORNING");
        executeChatRoom();


    }

    public void acceptClients(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            for (int i = 0; i < sharedData.numberOfPlayers; i++) {
                Socket connection = serverSocket.accept();
                logger.log("Client [" + i + "] connected successfully", LogLevels.INFO);
                pool.execute(new ClientHandler(connection));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeChatRoom() {
        for (ClientHandler player : sharedData.players) {
            pool.execute(new ChatHandler(player, this));
        }
    }

    public boolean checkIfEveryOneISReady() {
        for (ClientHandler player : sharedData.players) {
            if (!player.isReadyToPlay())
                return false;
        }
        if(sharedData.numberOfPlayers == sharedData.players.size())
            return true;
        else
            return false;
    }

    public void sendMessageToAllClients(String msg) {
        for (ClientHandler player : sharedData.players) {
            try {
                player.writeTxt(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessageToAllClientsExceptOne(String msg, ClientHandler notGiven) {

        for (ClientHandler player : sharedData.players) {
            if (!player.equals(notGiven)) {
                try {
                    player.writeTxt(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
        logger.log("introducing doc", LogLevels.INFO);
        String msg = sharedData.getSingleRole(PlayerRole.DR_CITY).getName() + " is doctor of city";
        sharedData.getSingleRole(PlayerRole.MAYOR).writeTxt(msg);
    }
}
