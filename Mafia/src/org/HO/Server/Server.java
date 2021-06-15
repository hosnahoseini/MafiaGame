package org.HO.Server;

import org.HO.*;
import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
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
    private static final int CHAT_TIME = 60000;
    private SharedData sharedData = SharedData.getInstance();
    private FileUtils fileUtils = new FileUtils();
    private ArrayList<String> events = new ArrayList<>();
    private static final LoggingManager logger = new LoggingManager(Server.class.getName());
    private ServerOutputHandling serverOutputHandling = new ServerOutputHandling();

    public Server(int numberOfPlayers) {
        sharedData.numberOfPlayers = numberOfPlayers;
        sharedData.calculateNumbers();
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

        waitUntilEveryOneReadyToPlay();

        introducing();

        gameLoop();

        announceWinner();

        endGame();
    }

    private void endGame() {
        for (Player player : sharedData.killedPlayers)
            player.close();
        for (Player player : sharedData.players)
            if (player.isAlive())
                player.close();
    }

    /**
     * announce winner to players
     */
    private void announceWinner() {
        sleep(1000);
        sendMessageToAllClients("Game ended");
        sendMessageToAllClients(sharedData.winner + " are the winners");
        sendMessageToAllClients("BYE");
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

            sendPollResultToAllClients(morningPoll);

            AskMayorForPoll();

            if (checkEndOfGame())
                break;

            sendMessageToAllClients("NIGHT");

            nightEvents();

            updateServer();

            sendMessageToAllClients("MORNING");

            sleep(1000);

            sendEventsToClient();

            sleep(1000);

        } while (!checkEndOfGame());
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.err.println("interrupting while sleeping");
        }
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

    /**
     * executing night events
     */
    private void nightEvents() {
        Poll mafiasPoll = executeMafiasPoll();

        godFatherChooseKilledOne(mafiasPoll);

        drLecterHealMafia();

        drCityHealCitizen();

        detectiveGuess();

        professionalKillMafia();

        psychologistMuteSO();

        dieHardInquired();

        NormalPeopleWakeUp();
    }

    /**
     * wake up normal people
     */
    private void NormalPeopleWakeUp() {
        for (Player player : sharedData.getNormalCitizens()) {
            try {
                player.writeTxt("YOUR TURN");
            } catch (IOException e) {
                disconnectHandling(player);
            }
        }
    }

    /**
     * send last night events to players
     */
    private void sendEventsToClient() {
        for (String event : events) {
            sendMessageToAllClients(event);
            logger.log("send " + event, LogLevels.INFO);
        }
    }

    /**
     * announce who are still alive
     */
    private void alivePlayerUpdate() {
        events.add("Alive players:");
        for (Player player : sharedData.getAlivePlayers())
            events.add(player.getName());
    }

    /**
     * update server and shared data regarding to last night events
     */
    private void updateServer() {
        events.clear();
        killedByMafiasUpdate();
        killedByProfessionalUpdate();
        inquiredUpdate();
        alivePlayerUpdate();
        muteUpdate();
        events.add(Color.RESET);
        events.add(0, Color.PURPLE);
    }

    /**
     * announce who is mute
     */
    private void muteUpdate() {
        if (sharedData.mute != null)
            events.add(sharedData.mute.getName() + " is muted");

    }

    /**
     * check if die hard requested for inquires add inquires to events
     */
    private void inquiredUpdate() {
        if (sharedData.killedInquired)
            events.add("killed roles are:\n" + sharedData.showKilledRoles());
    }

    /**
     * killed the player that professional wants to kill
     * and if it wasn't mafia kill professional itself!
     */
    private void killedByProfessionalUpdate() {
        Player killed = sharedData.killedByProfessional;
        Player professional = sharedData.getSingleRole(PlayerRole.PROFESSIONAL);
        if (killed != null) {
            if (killed.isMafia()
                    && (sharedData.healedMafia == null
                    || !(sharedData.healedMafia.equals(killed)))) {
                removePlayer(killed);
                events.add(killed + " have been killed last night");
            } else if (!killed.isMafia()
                    && professional != null
                    && (sharedData.healedCitizen == null
                    || !sharedData.healedCitizen.equals(professional))) {
                removePlayer(professional);
                events.add(professional
                        + " have been killed last night");
            }
        }
    }

    /**
     * killed the player that mafias want to kill
     */
    private void killedByMafiasUpdate() {
        Player killed = sharedData.killedByMafias;
        if (killed != null) {

            if (killed.getRole().equals(PlayerRole.DIE_HARD)) {
                if (sharedData.numberOfKillDieHard >= 1) {
                    removePlayer(killed);
                    events.add(killed + " have been killed last night");
                    return;
                } else {
                    sharedData.numberOfKillDieHard++;
                }

            } else if (!(killed.equals(sharedData.healedCitizen))) {
                removePlayer(killed);
                events.add(killed + " have been killed last night");
            } else {
                events.add("Mafias couldn't kill any one");
            }
        }
    }


    /**
     * check if the game ended or not
     *
     * @return true if game ended
     */
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

    /**
     * ask die hard if he want to inquire about roles
     */
    private void dieHardInquired() {

        Player dieHard = sharedData.getSingleRole(PlayerRole.DIE_HARD);
        if (dieHard != null && dieHard.isAlive()) {
            try {
                dieHard.writeTxt("YOUR TURN");

                dieHard.writeTxt("Do you want to know who has been killed?(y/n)");
                String result = serverOutputHandling.readWithExit(dieHard);
                if (result.equals("y")) {
                    if (sharedData.numberOfInquiries >= 2)
                        dieHard.writeTxt("you can't do it any more");
                    else {
                        dieHard.writeTxt("Okay");
                        sharedData.killedInquired = true;
                        sharedData.numberOfInquiries++;
                    }
                }else
                    dieHard.writeTxt("Okay");
            } catch (IOException e) {
                disconnectHandling(dieHard);
            }
        }

    }

    /**
     * ask psychologist if he wants to mute s.o.
     */
    private void psychologistMuteSO() {
        Player psychologist = sharedData.getSingleRole(PlayerRole.PSYCHOLOGIST);
        if (psychologist != null && psychologist.isAlive()) {
            try {
                psychologist.writeTxt("YOUR TURN");

                psychologist.writeTxt("Do you want to mute some one?(y/n)");
                String result = serverOutputHandling.readWithExit(psychologist);
                if (result.equals("y")) {
                    psychologist.writeTxt("who do you want to mute?");
                    try {
                        psychologist.getOutObj().writeObject(sharedData.getAlivePlayers());
                    } catch (IOException e) {
                        System.err.println("Can't send players name to psychologist");
                    }
                    String name = serverOutputHandling.readWithExit(psychologist);
                    Player player = sharedData.findPlayerWithName(name);
                    if (player != null)
                        player.setMute(true);
                    sharedData.mute = player;

                }
            } catch (IOException e) {
                disconnectHandling(psychologist);
            }
        }
    }

    /**
     * handle disconnection
     *
     * @param player disconnected player
     */
    private void disconnectHandling(Player player) {
        sharedData.players.remove(player);
        player.close();
    }

    /**
     * ask professional who does he wants to be killed
     */
    private void professionalKillMafia() {
        Player professional = sharedData.getSingleRole(PlayerRole.PROFESSIONAL);
        if (professional != null && professional.isAlive()) {
            try {
                professional.writeTxt("YOUR TURN");

                professional.writeTxt("Do you want to kill some one?(y/n)");
                String result = serverOutputHandling.readWithExit(professional);
                if (result.equals("y")) {
                    professional.writeTxt("who do you want to kill?");
                    try {
                        professional.getOutObj().writeObject(sharedData.getAlivePlayers());
                    } catch (IOException e) {
                        System.err.println("Can't send players name to professional");
                    }
                    String name = serverOutputHandling.readWithExit(professional);
                    sharedData.killedByProfessional = sharedData.findPlayerWithName(name);
                }
            } catch (IOException e) {
                disconnectHandling(professional);
            }
        }
    }

    /**
     * detective ask who is mafia
     */
    private void detectiveGuess() {
        Player detective = sharedData.getSingleRole(PlayerRole.DETECTIVE);
        if (detective != null && detective.isAlive()) {
            try {
                detective.writeTxt("YOUR TURN");
                detective.writeTxt("Who do you want to inquire about?");
                detective.getOutObj().writeObject(sharedData.getAlivePlayers());
                String name = serverOutputHandling.readWithExit(detective);
                Player player = sharedData.findPlayerWithName(name);
                if (player != null) {
                    if (player.isMafia() && (player.getRole() != PlayerRole.GOD_FATHER))
                        detective.writeTxt("he / she is mafia");
                    else
                        detective.writeTxt("he / she is NOT mafia");
                } else
                    detective.writeTxt(":(");

            } catch (IOException e) {
                disconnectHandling(detective);
            }
        }
    }

    /**
     * ask dr city who is gonna be healed
     */
    private void drCityHealCitizen() {
        Player drCity = sharedData.getSingleRole(PlayerRole.DR_CITY);
        if (drCity != null && drCity.isAlive())
            try {
                drCity.writeTxt("YOUR TURN");

                //check if there is possible choice
                if (sharedData.getCitizens().size() != 1 || drCity.getHeal() == 0) {

                    drCity.writeTxt("OK");

                    drCity.writeTxt("Who do you want to heal?");
                    try {
                        drCity.getOutObj().writeObject(sharedData.getCitizens());
                        while (true) {
                            String name = serverOutputHandling.readWithExit(drCity);
                            if (name.equals(drCity.getName()) && drCity.getHeal() == 1)
                                drCity.writeTxt("you've already healed your self, try another player:");
                            else {
                                drCity.writeTxt("thanks");

                                sharedData.healedCitizen = sharedData.findPlayerWithName(name);
                                if (sharedData.healedCitizen != null) {
                                    sharedData.healedCitizen.heal();
                                    if (sharedData.healedCitizen.getRole() == PlayerRole.DIE_HARD
                                            && sharedData.killedByMafias.getRole() == PlayerRole.DIE_HARD)
                                        sharedData.numberOfKillDieHard--;
                                }
                                break;
                            }

                        }
                    } catch (IOException e) {
                        System.err.println("Can't send citizens name to dr city");
                    }
                } else {
                    drCity.writeTxt("you don't have choice");
                }
            } catch (IOException e) {
                disconnectHandling(drCity);
            }
    }


    /**
     * ask dr lecter who is gonna be healed
     */
    private void drLecterHealMafia() {
        Player drLecter = sharedData.getSingleRole(PlayerRole.DR_LECTER);
        if (drLecter != null && drLecter.isAlive()) {
            try {
                drLecter.writeTxt("YOUR TURN");

                //check if there is possible choice
                if (sharedData.getMafias().size() != 1 || drLecter.getHeal() == 0) {

                    drLecter.writeTxt("OK");

                    drLecter.writeTxt("Who do you want to heal?");
                    try {
                        drLecter.getOutObj().writeObject(sharedData.getMafias());
                        while (true) {
                            String name = serverOutputHandling.readWithExit(drLecter);
                            if (name.equals(drLecter.getName()) && drLecter.getHeal() == 1)
                                drLecter.writeTxt("you've already healed your self, try another player:");
                            else {
                                drLecter.writeTxt("thanks");

                                sharedData.healedMafia = sharedData.findPlayerWithName(name);
                                if (sharedData.healedMafia != null)
                                    sharedData.healedMafia.heal();
                                break;
                            }

                        }
                    } catch (IOException e) {
                        System.err.println("Can't send mafias name to dr lecter");
                    }
                } else {
                    drLecter.writeTxt("you don't have choice");
                }
            } catch (IOException e) {
                disconnectHandling(drLecter);
            }
        }
    }

    /**
     * godfather decide who will be killed
     *
     * @param mafiasPoll mafias poll
     */
    private void godFatherChooseKilledOne(Poll mafiasPoll) {
        Player godFather = sharedData.getSingleRole(PlayerRole.GOD_FATHER);
        if (godFather != null && godFather.isAlive()) {
            try {
                godFather.writeTxt("YOUR TURN");
                godFather.writeTxt("This is the result of voting\nWho is going to be killed to night?");
                godFather.getOutObj().writeObject(mafiasPoll.getPoll().keySet());
                godFather.writeTxt(mafiasPoll.getPollResult());

                String killedName = serverOutputHandling.readWithExit(godFather);
                logger.log("read god father choice" + killedName, LogLevels.INFO);
                Player player = sharedData.findPlayerWithName(killedName);
                if (player != null)
                    sharedData.killedByMafias = player;
            } catch (IOException e) {
                disconnectHandling(godFather);
            }
        }
    }

    /**
     * send result of a poll to all players
     *
     * @param poll morning poll
     */
    private void sendPollResultToAllClients(Poll poll) {
        for (Player player : sharedData.players) {
            try {
                player.writeTxt(poll.getPollResult());
            } catch (IOException e) {
                disconnectHandling(player);
            }

        }
    }

    /**
     * a poll for mafias to decide who is going to be killed
     *
     * @return mafias poll
     */
    private Poll executeMafiasPoll() {
        for (Player player : sharedData.getMafias()) {
            try {
                player.writeTxt("YOUR TURN");
            } catch (IOException e) {
                disconnectHandling(player);
            }
        }
        Poll poll = executePoll(sharedData.getCitizens(), sharedData.getMafias());
        sharedData.killedByMafias = poll.winner();
        return poll;
    }

    /**
     * execute a poll
     *
     * @param choices poll choices
     * @param voters  poll voters
     * @return poll
     */
    private Poll executePoll(Collection<Player> choices, Collection<Player> voters) {
        ExecutorService pool = Executors.newCachedThreadPool();
        Poll poll = new Poll(choices);
        for (Player player : voters) {
            pool.execute(new PollHandler(poll, player));
        }

        try {
            pool.shutdown();
            pool.awaitTermination(30050, TimeUnit.MILLISECONDS);


        } catch (InterruptedException e) {
            System.err.println("pool termination caused intruotion");
        }
        return poll;

    }

    /**
     * ask mayor if he wants to cancel morning poll or not
     */
    private void AskMayorForPoll() {
        Player mayor = sharedData.getSingleRole(PlayerRole.MAYOR);
        if (mayor != null && mayor.isAlive()) {
            try {
                mayor.writeTxt("YOUR TURN");
                mayor.writeTxt(sharedData.killed.getName() + " is going to be killed do you want to cancel this?(y/n)");
                String result = serverOutputHandling.readWithExit(mayor);
                if (result.equals("y")) {
                    sharedData.killed = null;
                    sendMessageToAllClients("Mayor canceled poll");
                } else {
                    removePlayer(sharedData.killed);
                    sendMessageToAllClients(sharedData.killed + " killed");
                }
            } catch (IOException e) {
                disconnectHandling(mayor);
            }
        }
    }

    /**
     * remove a player from game
     *
     * @param killed player to be removed
     */
    private void removePlayer(Player killed) {
        try {
            if (killed.isAlive()) {
                sharedData.killedPlayers.add(killed);
                killed.setAlive(false);

                killed.writeTxt("You've been killed:(");
                killed.writeTxt("Do you want to see rest of the game?(y/n)");
                logger.log(killed + " killed" + killed, LogLevels.INFO);
                String result = killed.readTxt();
                if (result.equals("n")) {
                    disconnectHandling(killed);
                    try {
                        killed.getConnection().close();
                    } catch (IOException e) {
                        System.err.println("Some thing went wrong with Server in I/O while closing connection of player " + killed);
                    }
                }
            }
        } catch (IOException e) {
            disconnectHandling(killed);
        }
    }

    /**
     * execute morning poll to remove s.o. from game
     *
     * @return morning poll
     */
    private Poll executeMorningPolling() {
        Poll poll = executePoll(sharedData.getAlivePlayers(), sharedData.getAlivePlayers());
        sendMessageToAllClients("VOTING TIME ENDED");
        sleep(1000);
        sharedData.killed = poll.winner();
        return poll;
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
        try {
            pool.shutdown();
            boolean result = pool.awaitTermination(CHAT_TIME + 50, TimeUnit.MILLISECONDS);
            if (!result) {
                sendMessageToAllClients("Chat time ended");
            }
        } catch (InterruptedException e) {
            System.err.println("pool termination caused interruption");
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
    private void waitUntilEveryOneReadyToPlay() {
        while (true) {
            if (checkIfEveryOneISReady())
                break;
        }
    }

    /**
     * check if all the players are ready
     *
     * @return true if the are ready
     */
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
            try {
                player.writeTxt(msg);
            } catch (IOException e) {
                disconnectHandling(player);
            }
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
                    try {
                        mafia.writeTxt(otherMafia.getName() + " is " + otherMafia.getRole());
                    } catch (IOException e) {
                        disconnectHandling(mafia);
                    }
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
        try {
            sharedData.getSingleRole(PlayerRole.MAYOR).writeTxt(msg);
        } catch (IOException e) {
            disconnectHandling(sharedData.getSingleRole(PlayerRole.MAYOR));
        }
    }


}
