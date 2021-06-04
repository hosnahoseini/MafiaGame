package org.HO.Server;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;

import org.HO.Player;
import org.HO.PlayerRole;
import org.HO.Poll;
import org.HO.SharedData;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private SharedData sharedData = SharedData.getInstance();
    private static final LoggingManager logger = new LoggingManager(Server.class.getName());

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

        Poll morningPoll = MorningPolling();

        sendMessageToAllClients("VOTING TIME ENDED");

        sendPollResultToAllClients(morningPoll);

//        AskMayorForPoll();

        sendMessageToAllClients("NIGHT");

        Poll mafiasPoll = mafiasPoll();

        godFatherChoiceKilledOne(mafiasPoll);

//        drLecterHealMafia();
    }

    private void godFatherChoiceKilledOne(Poll mafiasPoll) {
        Player godFather = sharedData.getSingleRole(PlayerRole.GOD_FATHER);
        godFather.writeTxt("YOUR TURN");
        if(godFather != null){
            try {
                godFather.writeTxt("This is the result of voting\nWho is going to be killed to night?");
                godFather.getOutObj().writeObject(mafiasPoll);
                String killedName = godFather.readTxt();
                sharedData.killedByMafias = sharedData.findPlayerWithName(killedName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPollResultToAllClients(Poll poll) {
        for (Player player : sharedData.players) {
            try {
                player.getOutObj().writeObject(poll.toString());
                logger.log("write poll res",LogLevels.INFO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Poll mafiasPoll() {
        ExecutorService pool = Executors.newCachedThreadPool();
        Poll poll = new Poll(sharedData.getCitizens());
        for (Player player : sharedData.getMafias()) {
            player.writeTxt("YOUR TURN");
            pool.execute(new PollHandler(poll, player));
        }
        try {
            pool.shutdown();
            pool.awaitTermination(30, TimeUnit.SECONDS);
            poll.showResult();
            System.out.println(poll.winner().getName());
            return poll;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void AskMayorForPoll() {
        Player mayor = sharedData.getSingleRole(PlayerRole.MAYOR);
        if(mayor != null) {
            mayor.writeTxt(sharedData.killed.getName() + " is going to be killed do you want to cancel this?(y/n)");
            String result = mayor.readTxt();
            if (result == "y") {
                sharedData.killed = null;
            } else
                removePlayer(sharedData.killed);
        }
    }

    private void removePlayer(Player killed) {
        sharedData.killedPlayers.add(killed);
        killed.setAlive(false);

        killed.writeTxt("You've been killed:(");
        killed.writeTxt("Do you want to see rest of the game?(y/n)");
        String result = killed.readTxt();
        if (result == "n") {
            killed.setAbleToReadChat(false);
            killed.writeTxt("OK BYE!");
            sharedData.players.remove(killed);
        }
    }

    private Poll MorningPolling() {
        ExecutorService pool = Executors.newCachedThreadPool();
        Poll poll = new Poll(sharedData.players);
        for (Player player : sharedData.getAlivePlayers()) {
            pool.execute(new PollHandler(poll, player));
        }
        try {
            pool.shutdown();
            pool.awaitTermination(30, TimeUnit.SECONDS);
            sharedData.killed = poll.winner();
            return poll;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void acceptClients(int port) {
        ExecutorService pool = Executors.newCachedThreadPool();
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
        ExecutorService pool = Executors.newCachedThreadPool();
        for (Player player : sharedData.players) {
            if (player.isAlive() || player.isAbleToReadChat())
                pool.execute(new ChatHandler(player));
        }

        try {
            pool.shutdown();
            pool.awaitTermination(40, TimeUnit.SECONDS);

            sendMessageToAllClients("Chat time ended");
            sendMessageToAllClients("POLL");


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public boolean checkIfEveryOneISReady() {
        for (Player player : sharedData.players) {
            if (!player.isReadyToPlay())
                return false;
        }
        if (sharedData.numberOfPlayers == sharedData.players.size())
            return true;
        else
            return false;
    }

    public void sendMessageToAllClients(String msg) {
        sendMessageToAGroup(sharedData.players, msg);
    }

    public void sendMessageToAGroup(Collection<Player> members, String msg) {
        for (Player member : members) {
            try {
                member.getOut().flush();
                member.writeTxt(msg);
            } catch (IOException e) {
                e.printStackTrace();
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
        for (Player mafia : sharedData.getMafias()) {

            logger.log("introducing mafias", LogLevels.INFO);
            for (Player otherMafia : sharedData.getMafias()) {
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
