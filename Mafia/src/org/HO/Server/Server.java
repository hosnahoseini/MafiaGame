package org.HO.Server;

import org.HO.*;
import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A class for server which handle game process
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class Server {
    private SharedData sharedData = SharedData.getInstance();
    private FileUtils fileUtils = new FileUtils();
    private ArrayList<String> events = new ArrayList<>();
    private static final LoggingManager logger = new LoggingManager(Server.class.getName());

    public Server(int numberOfPlayers) {
        sharedData.numberOfPlayers = numberOfPlayers;
    }

    public Server(SharedData sharedData) {
        this.sharedData = sharedData;
    }

    /**
     * start server for a new game
     *
     * @param port game port
     */
    public void start(int port) {

        acceptClients(port);

        resetChatBox("chatBox.txt");

        waitUntilEveryOneReady();

        introducing();

        gameLoop();
    }

    /**
     * loop for the main part of game
     */
    private void gameLoop() {

        do {

            sharedData.reset();

            sendMessageToAllClients("CHAT TIME");

            executeChatRoom();

            unMutePlayers();

            sendMessageToAllClients("POLL");

            Poll morningPoll = executeMorningPolling();

            sendMessageToAllClients("VOTING TIME ENDED");

            sendPollResultToAllClients(morningPoll);

            AskMayorForPoll();

            if (checkEndOfGame())
                break;

            sendMessageToAllClients("NIGHT");

            nightEvents();

            updateServer();

            sendMessageToAllClients("MORNING");

            sendEventsToClient();

        } while (!checkEndOfGame());
    }


    /**
     * empty files which act like storage of chat box
     *
     * @param fileName file name of chat box to be empty
     */
    private void resetChatBox(String fileName) {
        File file = new File(fileName);
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print("--CHAT BOX--\n");

            if (fileName.equals("chatBox.txt"))
                writer.print("chat box is empty at first\n");

            writer.close();
        } catch (FileNotFoundException e) {
            System.err.println("There is not such a file");
        }

    }

    private void nightEvents() {
        Poll mafiasPoll = mafiasPoll();

        godFatherChooseKilledOne(mafiasPoll);

        drLecterHealMafia();

        drCityHealCitizen();

        detectiveGuess();

        professionalKillMafia();

        psychologistMuteSO();

        dieHardInquired();
    }

    private void sendEventsToClient() {
        for (String event : events) {
            sendMessageToAllClients(event);
            logger.log("send " + event, LogLevels.INFO);
        }
    }

    private void alivePlayerUpdate() {
        events.add("Alive players:\n");
        for (Player player : sharedData.getAlivePlayers())
            events.add(player.getName());
    }

    private void updateServer() {
        events.clear();
        killedByMafiasUpdate();
        killedByProfessionalUpdate();
        inquiredUpdate();
        alivePlayerUpdate();
        muteUpdate();
    }

    private void muteUpdate() {
        if (sharedData.mute != null)
            events.add(sharedData.mute.getName() + " is muted");
    }

    private void inquiredUpdate() {
        if (sharedData.killedInquired) {
            events.add("killed roles are:\n" + sharedData.showKilledRoles());
            logger.log("killed roles are:\n" + sharedData.showKilledRoles(), LogLevels.INFO);

        }
    }

    private void killedByProfessionalUpdate() {
        Player killed = sharedData.killedByProfessional;
        Player professional = sharedData.getSingleRole(PlayerRole.PROFESSIONAL);
        if (killed != null) {
            if (killed.isMafia()
                    && !(sharedData.healedMafia.equals(killed))) {
                removePlayer(killed);
                events.add(killed + " have been killed last night");
                logger.log(killed + " have been killed last night", LogLevels.INFO);

            }
            if (!killed.isMafia()
                    && professional != null
                    && !sharedData.healedCitizen.equals(professional)) {
                removePlayer(professional);
                events.add(professional
                        + " have been killed last night");
                logger.log(professional
                        + " have been killed last night", LogLevels.INFO);

            }
        }
    }

    private void killedByMafiasUpdate() {
        Player killed = sharedData.killedByMafias;
        if (killed != null) {
            if (killed.getRole().equals(PlayerRole.DIE_HARD)) {
                if (sharedData.numberOfKillDieHard >= 1) {
                    System.out.println("-->" + sharedData.numberOfKillDieHard);
                    removePlayer(killed);
                    return;
                } else {
                    sharedData.numberOfKillDieHard++;
                }
            } else if (!(killed.equals(sharedData.healedCitizen))) {
                removePlayer(killed);
                events.add(killed + " have been killed last night");
                logger.log(killed + " have been killed last night", LogLevels.INFO);
            } else {
                events.add("Mafias couldn't kill any one");
                logger.log("Mafias couldn't kill any one", LogLevels.INFO);

            }
        }
    }


    private boolean checkEndOfGame() {
        if (sharedData.getMafias().size() >= sharedData.getCitizens().size()) {
            sharedData.winner = PlayerRole.MAFIAS;
            return true;
        }
        if (sharedData.getMafias().size() == 0) {
            sharedData.winner = PlayerRole.CITIZENS;
            return true;
        }
        return false;
    }

    private void dieHardInquired() {

        Player dieHard = sharedData.getSingleRole(PlayerRole.DIE_HARD);
        if (dieHard != null && dieHard.isAlive()) {
            dieHard.writeTxt("YOUR TURN");

            dieHard.writeTxt("Do you want to know who has been killed?(y/n)");
            String result = readWithExit(dieHard);
            if (result.equals("y")) {
                if (sharedData.numberOfInquiries == 2)
                    dieHard.writeTxt("you can't do it any more");
                else {
                    dieHard.writeTxt("Okay");
                    sharedData.killedInquired = true;
                    sharedData.numberOfInquiries++;
                }
            }
        }
    }

    private void psychologistMuteSO() {
        Player psychologist = sharedData.getSingleRole(PlayerRole.PSYCHOLOGIST);
        if (psychologist != null && psychologist.isAlive()) {
            psychologist.writeTxt("YOUR TURN");

            psychologist.writeTxt("Do you want to mute some one?(y/n)");
            String result = readWithExit(psychologist);
            if (result.equals("y")) {
                psychologist.writeTxt("who do you want to mute?");
                try {
                    psychologist.getOutObj().writeObject(sharedData.getAlivePlayers());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String name = readWithExit(psychologist);
                Player player = sharedData.findPlayerWithName(name);
                player.setMute(true);
                sharedData.mute = player;
            }

        }
    }

    private void professionalKillMafia() {
        Player professional = sharedData.getSingleRole(PlayerRole.PROFESSIONAL);
        if (professional != null && professional.isAlive()) {
            professional.writeTxt("YOUR TURN");

            professional.writeTxt("Do you want to kill some one?(y/n)");
            String result = readWithExit(professional);
            if (result.equals("y")) {
                professional.writeTxt("who do you want to kill?");
                try {
                    professional.getOutObj().writeObject(sharedData.getAlivePlayers());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String name = readWithExit(professional);
                Player player = sharedData.findPlayerWithName(name);
                sharedData.killedByProfessional = player;
            }

        }
    }

    private void detectiveGuess() {
        Player detective = sharedData.getSingleRole(PlayerRole.DETECTIVE);
        if (detective != null && detective.isAlive()) {
            detective.writeTxt("YOUR TURN");
            try {
                detective.writeTxt("Who do you want to inquire about?");
                detective.getOutObj().writeObject(sharedData.getAlivePlayers());
                String name = readWithExit(detective);
                Player player = sharedData.findPlayerWithName(name);
                if (player.isMafia() && (player.getRole() != PlayerRole.GOD_FATHER))
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
        if (drCity != null && drCity.isAlive()) {
            drCity.writeTxt("YOUR TURN");
            if (sharedData.getCitizens().size() != 1 || drCity.getHeal() == 0) {
                drCity.writeTxt("OK");
                logger.log("send OK", LogLevels.INFO);
                while (true) {
                    drCity.writeTxt("Who do you want to heal?");
                    try {
                        drCity.getOutObj().writeObject(sharedData.getCitizens());
                        String name = readWithExit(drCity);
                        if (name.equals(drCity.getName()) && drCity.getHeal() == 1)
                            drCity.writeTxt("you've already healed your self, try another player:");
                        else {
                            drCity.writeTxt("thanks");

                            sharedData.healedCitizen = sharedData.findPlayerWithName(name);
                            sharedData.healedCitizen.heal();
                            if (sharedData.healedCitizen.getRole() == PlayerRole.DIE_HARD
                                    && sharedData.killedByMafias.getRole() == PlayerRole.DIE_HARD)
                                sharedData.numberOfKillDieHard--;
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                drCity.writeTxt("you don't have choice");
            }
        }
    }

    private void drLecterHealMafia() {
        Player drLecter = sharedData.getSingleRole(PlayerRole.DR_LECTER);
        if (drLecter != null && drLecter.isAlive()) {
            drLecter.writeTxt("YOUR TURN");
            if (sharedData.getMafias().size() != 1 || drLecter.getHeal() == 0) {
                drLecter.writeTxt("OK");
                while (true) {
                    drLecter.writeTxt("Who do you want to heal?");
                    try {
                        drLecter.getOutObj().writeObject(sharedData.getMafias());
                        String name = readWithExit(drLecter);
                        if (name.equals(drLecter.getName()) && drLecter.getHeal() == 1)
                            drLecter.writeTxt("you've already healed your self, try another player:");
                        else {
                            drLecter.writeTxt("thanks");

                            sharedData.healedMafia = sharedData.findPlayerWithName(name);
                            sharedData.healedMafia.heal();
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                drLecter.writeTxt("you don't have choice");
            }
        }
    }

    private void godFatherChooseKilledOne(Poll mafiasPoll) {
        Player godFather = sharedData.getSingleRole(PlayerRole.GOD_FATHER);
        if (godFather != null && godFather.isAlive()) {
            try {
                godFather.writeTxt("YOUR TURN");

                godFather.writeTxt("This is the result of voting\nWho is going to be killed to night?");

                godFather.getOutObj().writeObject(mafiasPoll.getPoll().keySet());
                godFather.writeTxt(mafiasPoll.PollResult());

                String killedName = godFather.readTxt();
                logger.log("read god father choice" + killedName, LogLevels.INFO);
                sharedData.killedByMafias = sharedData.findPlayerWithName(killedName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * send result of a poll to all players
     * @param poll morning poll
     */
    private void sendPollResultToAllClients(Poll poll) {
        for (Player player : sharedData.players) {
            player.writeTxt(poll.PollResult());
            logger.log("write poll res", LogLevels.INFO);

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
            pool.awaitTermination(40000, TimeUnit.SECONDS);
            sharedData.killedByMafias = poll.winner();
            return poll;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ask mayor if he wants to cancel morning poll or not
     */
    private void AskMayorForPoll() {
        Player mayor = sharedData.getSingleRole(PlayerRole.MAYOR);
        if (mayor != null && mayor.isAlive()) {
            mayor.writeTxt("YOUR TURN");
            mayor.writeTxt(sharedData.killed.getName() + " is going to be killed do you want to cancel this?(y/n)");
            String result = readWithExit(mayor);
            if (result.equals("y")) {
                sharedData.killed = null;
                sendMessageToAllClients("Mayor canceled poll");
            } else {
                removePlayer(sharedData.killed);
                sendMessageToAllClients(sharedData.killed + " killed");
            }
        }
    }

    /**
     * remove a player from game
     * @param killed player to be removed
     */
    private void removePlayer(Player killed) {

        if (killed.isAlive()) {
            sharedData.killedPlayers.add(killed);
            killed.setAlive(false);

            killed.writeTxt("You've been killed:(");
            killed.writeTxt("Do you want to see rest of the game?(y/n)");
            logger.log(killed + " killed" + killed, LogLevels.INFO);
            String result = killed.readTxt();
            if (result.equals("n")) {
                killed.setAbleToReadChat(false);
                //killed.writeTxt("OK BYE!");
                sharedData.players.remove(killed);
                try {
                    killed.getConnection().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * execute morning poll to remove s.o. from game
     *
     * @return morning poll
     */
    private Poll executeMorningPolling() {
        ExecutorService pool = Executors.newCachedThreadPool();
        Poll poll = new Poll(sharedData.getAlivePlayers());
        for (Player player : sharedData.getAlivePlayers()) {
            pool.execute(new PollHandler(poll, player));
        }
        try {
            pool.shutdown();
            pool.awaitTermination(4000, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
            //TODO:handle exp
        } finally {
            sharedData.killed = poll.winner();
            return poll;
        }
    }

    /**
     * accept clients(game players)
     *
     * @param port game port
     */
    private void acceptClients(int port) {
        ExecutorService pool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            for (int i = 0; i < sharedData.numberOfPlayers; i++) {
                Socket connection = serverSocket.accept();
                logger.log("Client [" + i + "] connected successfully", LogLevels.INFO);
                pool.execute(new ClientHandler(connection));
            }

        } catch (IOException e) {
            System.err.println("Some thing went wrong with Server in I/O");
        }
    }

    /**
     * start chat room(start new chat handler thread for each player)
     */
    public void executeChatRoom() {

        resetChatBox("chatBoxTemp.txt");

        ExecutorService pool = Executors.newCachedThreadPool();
        for (Player player : sharedData.players) {
            if (player.isAlive() && !player.isMute()) {
                pool.execute(new ChatHandler(player));
            }
        }
        pool.shutdown();
        try {
            if (!pool.awaitTermination(4000000, TimeUnit.SECONDS))
                sendMessageToAllClients("Chat time ended");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("InterruptedException for termination");
            //TODO:handle this exp
        }

        fileUtils.copy("chatBoxTemp.txt", "chatBox.txt");
    }

    /**
     * unmute the player who have been mute previous turn for chat
     */
    private void unMutePlayers() {
        for (Player player : sharedData.players)
            if (player.isMute())
                player.setMute(false);
    }

    /**
     * server wait until all clients join game
     */
    private void waitUntilEveryOneReady() {
        while (true) {
            if (checkIfEveryOneISReady())
                break;
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

    /**
     * send text message to all players
     *
     * @param msg message to be sent
     */
    public void sendMessageToAllClients(String msg) {

        for (Player player : sharedData.players) {
            //player.getOut().flush();
            player.writeTxt(msg);
            logger.log("send " + msg + " to " + player.getName(), LogLevels.INFO);
        }
    }

    /**
     * introducing players to each other
     */
    public void introducing() {
        introduceMafias();
        introduceDrCityToMayor();
    }

    /**
     * Mafias introducing
     */
    public void introduceMafias() {
        for (Player mafia : sharedData.getMafias()) {
            logger.log("introducing mafias", LogLevels.INFO);
            for (Player otherMafia : sharedData.getMafias()) {
                if (!otherMafia.equals(mafia)) {
                    mafia.writeTxt(otherMafia.getName() + " is " + otherMafia.getRole());
                }
            }

        }
    }

    /**
     * introduce dr city to mayor
     */
    public void introduceDrCityToMayor() {
        logger.log("introducing doctor city", LogLevels.INFO);
        String msg = sharedData.getSingleRole(PlayerRole.DR_CITY).getName() + " is doctor of city";
        sharedData.getSingleRole(PlayerRole.MAYOR).writeTxt(msg);
    }

    public String readWithExit(Player player) {
        String input = "";
        try {
            input = player.getIn().readUTF();
            if (input.equals("exit"))
                removePlayer(player);
        } catch (SocketException e) {
            player.close();
            sharedData.players.remove(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }


}
