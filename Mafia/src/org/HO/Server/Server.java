package org.HO.Server;

import org.HO.Client.Role.GodFather;
import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;

import org.HO.Player;
import org.HO.PlayerRole;
import org.HO.Poll;
import org.HO.SharedData;

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

        sendMessageToAllClients("POLL");

        Poll morningPoll = MorningPolling();

        sendMessageToAllClients("VOTING TIME ENDED");

        sendPollResultToAllClients(morningPoll);

//        AskMayorForPoll();

        sendMessageToAllClients("NIGHT");

        Poll mafiasPoll = mafiasPoll();

        godFatherChooseKilledOne(mafiasPoll);

        drLecterHealMafia();

        drCityHealCitizen();

        detectiveGuess();

        professionalKillMafia();

        psychologistMuteSO();
    }

    private void psychologistMuteSO() {
        Player psychologist = sharedData.getSingleRole(PlayerRole.PSYCHOLOGIST);
        psychologist.writeTxt("YOUR TURN");
        if (psychologist != null) {

            psychologist.writeTxt("Do you want to mute some one?(y/n)");
            String result = psychologist.readTxt();
            if (result.equals("y")) {
                psychologist.writeTxt("who do you want to mute?");
                try {
                    psychologist.getOutObj().writeObject(sharedData.getAlivePlayers());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String name = psychologist.readTxt();
                Player player = sharedData.findPlayerWithName(name);
                sharedData.killedByProfessional = player;
            }

        }
    }

    private void professionalKillMafia() {
        Player professional = sharedData.getSingleRole(PlayerRole.PROFESSIONAL);
        professional.writeTxt("YOUR TURN");
        if (professional != null) {

            professional.writeTxt("Do you want to kill some one?(y/n)");
            String result = professional.readTxt();
            if (result.equals("y")) {
                professional.writeTxt("who do you want to kill?");
                try {
                    professional.getOutObj().writeObject(sharedData.getAlivePlayers());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String name = professional.readTxt();
                Player player = sharedData.findPlayerWithName(name);
                sharedData.killedByProfessional = player;
            }

        }
    }

    private void detectiveGuess() {
        Player detective = sharedData.getSingleRole(PlayerRole.DETECTIVE);
        detective.writeTxt("YOUR TURN");
        if (detective != null) {
            try {
                detective.writeTxt("Who do you want to inquire about?");
                detective.getOutObj().writeObject(sharedData.getAlivePlayers());
                String name = detective.readTxt();
                Player player = sharedData.findPlayerWithName(name);
                if (player.isMafia() && !(player.getRole() != PlayerRole.GOD_FATHER))
                    detective.writeTxt("he / she is mafia");
                else
                    detective.writeTxt("he / she is NOT mafia");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void drCityHealCitizen() {
        Player drCity = sharedData.getSingleRole(PlayerRole.DR_CITY);
        drCity.writeTxt("YOUR TURN");
        if (drCity != null) {
            while (true) {
                drCity.writeTxt("Who do you want to heal?");
                try {
                    drCity.getOutObj().writeObject(sharedData.getCitizens());
                    String name = drCity.readTxt();
                    if (name.equals(drCity) && drCity.getHeal() == 1)
                        drCity.writeTxt("you've already healed your self, try another player:");
                    else {
                        drCity.writeTxt("thanks");
                        break;
                    }
                    sharedData.healedMafia = sharedData.findPlayerWithName(name);
                    sharedData.healedMafia.heal();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void drLecterHealMafia() {
        Player drLecter = sharedData.getSingleRole(PlayerRole.DR_LECTER);
        drLecter.writeTxt("YOUR TURN");
        if (drLecter != null) {
            while (true) {
                drLecter.writeTxt("Who do you want to heal?");
                try {
                    drLecter.getOutObj().writeObject(sharedData.getMafias());
                    String name = drLecter.readTxt();
                    if (name.equals(drLecter) && drLecter.getHeal() == 1)
                        drLecter.writeTxt("you've already healed your self, try another player:");
                    else {
                        drLecter.writeTxt("thanks");
                        break;
                    }
                    sharedData.healedMafia = sharedData.findPlayerWithName(name);
                    sharedData.healedMafia.heal();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("BYE LECTER");
    }

    private void godFatherChooseKilledOne(Poll mafiasPoll) {
        Player godFather = sharedData.getSingleRole(PlayerRole.GOD_FATHER);
        godFather.writeTxt("YOUR TURN");
        if (godFather != null) {

            godFather.writeTxt("This is the result of voting\nWho is going to be killed to night?");
            godFather.writeTxt(mafiasPoll.PollResult());
            String killedName = godFather.readTxt();
            sharedData.killedByMafias = sharedData.findPlayerWithName(killedName);

        }
    }

    private void sendPollResultToAllClients(Poll poll) {
        for (Player player : sharedData.players) {
            try {
                player.getOutObj().writeObject(poll.PollResult());
                logger.log("write poll res", LogLevels.INFO);
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
        if (mayor != null) {
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
            if (!pool.awaitTermination(40, TimeUnit.SECONDS))
                sendMessageToAllClients("Chat time ended");

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
                logger.log("send " + msg + " to " + member.getName(), LogLevels.INFO);
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
