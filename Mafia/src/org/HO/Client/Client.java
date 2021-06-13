package org.HO.Client;

import org.HO.Client.Role.ClientFactory;
import org.HO.Client.Role.ClientInputHandling;
import org.HO.Client.Role.ClientWithRole;
import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.PlayerRole;
import org.HO.Player;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * A class for client
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */

public class Client {

    private Player player;
    private Scanner scanner = new Scanner(System.in);
    private ClientWithRole clientWithRole;
    private ClientFactory factory = new ClientFactory();
    private static final LoggingManager logger = new LoggingManager(Client.class.getName());
    private boolean running = true;
    private String vote = "";
    protected ClientInputHandling clientInputHandling = new ClientInputHandling();


    /**
     * start client to play
     *
     * @param ipAddress ip address of the server
     * @param port      port of the game
     */
    public void startClient(String ipAddress, int port) {

        Socket connection = null;
        try {
            connection = new Socket(ipAddress, port);
        } catch (IOException e) {
            System.err.println("Some went Wrong in I/O");
        }

        player = new Player(connection);
        System.out.println("connected to server");

        initializeInfo();

        gameLoop();

        if (!player.isAlive())
            while (true) {
                String input = player.readTxt();
                System.out.println(input);
                if(input.equals("Game ended")){
                    System.out.println(player.readTxt());
                    System.out.println(player.readTxt());
                    player.close();
                    System.exit(2);
                }
            }

    }

    /**
     * main loop of game
     */
    private void gameLoop() {
        do {
            ReceiveUntilGetMsg("CHAT TIME");
            System.out.println("~~~CHAT STARTED~~~");
            startChat();

            waitUntilRecivingMsg("POLL");

            voteForMorningPoll();

            waitUntilRecivingMsg("VOTING TIME ENDED");

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            showPollResult();

            if (player.getRole() == PlayerRole.MAYOR)
                clientWithRole.start();

            ReceiveUntilGetMsg("NIGHT");
            System.out.println("     ██▀██▀█▀█▀▀██▀█▀█▀▀▀█\n" +
                    "     ██─▄▀─█─█─█▀█─▄─██─██\n" +
                    "     ██▄██▄█▄█▄▄▄█▄█▄██▄██ ");
            if (!player.isAlive())
                break;

            if (player.getRole() != PlayerRole.MAYOR)
                clientWithRole.start();

            ReceiveUntilGetMsg("MORNING");
            System.out.println("█▄ ▄█ ▄▀▀▄ █▀▄ █▄ █ █ █▄ █ ▄▀▀\n" +
                    "█ ▀ █ █  █ ██▀ █ ▀█ █ █ ▀█ █ ▀█\n" +
                    "▀   ▀  ▀▀  ▀ ▀ ▀  ▀ ▀ ▀  ▀ ▀▀▀▀");

            if (!player.isAlive())
                break;

            System.out.println("~~~last night events~~~");
        } while (true);
    }

    private void showPollResult() {
        String poll = player.readTxt();
        logger.log("read poll res", LogLevels.INFO);
        System.out.println("The result is:");
        System.out.println(poll);

    }

    private void voteForMorningPoll() {
        try {
            logger.log("start poll " + player.getName(), LogLevels.INFO);

            System.out.println(player.readTxt());
            Collection<Player> poll = (Collection<Player>) player.getInObj().readObject();
            logger.log("receive poll " + player.getName(), LogLevels.INFO);
            for (Player player : poll)
                System.out.println(player);

            getInputForVote(poll);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getInputForVote(Collection<Player> poll) {
        vote = "";
        running = true;
        Timer timer = new Timer();
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                running = false;
                if (validInput(poll, vote)) {
                    player.writeTxtClient(vote);
                } else
                    player.writeTxtClient("");
                timer.cancel();
            }
        };

        timer.schedule(task, 20000);
        try {
            while (running) {
                while (!scanner.ready()) {
                    Thread.sleep(50);
                    if (!running)
                        return;
                }
                vote = scanner.readLine();
                if(clientInputHandling.checkIfInputIsExit(player, vote))
                    timer.cancel();

                if (!validInput(poll, vote)) {
                    System.out.println("Invalid input!Try again");
                    vote = "";
                } else
                    System.out.println("thanks");

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("");
        }
    }

    private boolean validInput(Collection<Player> choices, String name) {
        for (Player player : choices)
            if (player.getName().equalsIgnoreCase(name) && !(name.equals(this.player.getName())))
                return true;
        return false;
    }

    private void startChat() {

        if (player.isMute()) {
            System.out.println("you are mute this turn");
            player.setMute(false);
            //TODO:flush buffer
            return;
        }

        Thread read = new Thread(new ReadThread(player));
        Thread write = new WriteThread(player);
        write.start();
        read.start();

        try {
            read.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * receive and print any message that comes from server until receiving specific message
     *
     * @param msg specific message
     */
    private void ReceiveUntilGetMsg(String msg) {
        while (true) {
            String input = player.readTxt();
            if (input != null) {
                logger.log(player.getName() + " read " + input, LogLevels.INFO);
                if (input.equals(msg)) {
                    break;
                }else
                    System.out.println(input);
                receiverKilledMessage(input);
                receiverMuteMessage(input);
                receiverWinner(input);
            }
        }
    }

    /**
     * receiving winner from server
     *
     * @param input messages that come from server
     */
    private void receiverWinner(String input) {
        if (input.equals("Game ended")) {
            System.out.println(player.readTxt());
            System.out.println(player.readTxt());
            player.close();
            System.exit(9);
        }
    }

    /**
     * receiving mute message from server
     *
     * @param input messages that come from server
     */
    private void receiverMuteMessage(String input) {
        if (input.contains(player.getName() + " is muted"))
            player.setMute(true);
        else
            player.setMute(false);
    }

    /**
     * receiving killed message from server
     *
     * @param input messages that come from server
     */
    private void receiverKilledMessage(String input) {
        if (input.contains("You've been killed:(")) {
            System.out.println(player.readTxt());
            String result = scanner.next();
            player.writeTxtClient(result);
            player.setAlive(false);
            if (result.equals("n")) {
                System.out.println("BYE");
                player.close();
                System.exit(0);
            }
        }
    }

    /**
     * receive any thing from server until getting a special message
     *
     * @param msg special message
     */
    private void waitUntilRecivingMsg(String msg) {
        while (true) {
            String input = player.readTxt();
            if (input.equals(msg)) {
                System.out.println("~~~" + msg + "~~~");
                logger.log("read" + msg, LogLevels.INFO);
                break;
            }
        }
    }

    /**
     * initialize info of player
     */
    private void initializeInfo() {
        setName();
        try {
            player.setRole((PlayerRole) player.getInObj().readObject());
            System.out.println("Welcome to our game \nyour role is -> " + player.getRole());
            System.out.println("type GO whenever you are ready to play");
            //scanner.next();
            player.getOutObj().writeObject(true);
            System.out.println("FIRST MORNING");
            clientWithRole = factory.getClient(player);
        } catch (IOException e) {
            System.err.println("Can't read role or write boolean");
        } catch (ClassNotFoundException e) {
            System.err.println("Can't convert to role");
        }
    }

    /**
     * get name from user and set it
     */
    private void setName() {
        System.out.println("Enter your name: ");
        String name;
        while (true) {
            name = scanner.nextLine();
            player.writeTxtClient(name);
            try {
                if ((boolean) player.getInObj().readObject())
                    break;
            } catch (IOException e) {
                System.err.println("Can't read boolean");
            } catch (ClassNotFoundException e) {
                System.err.println("Can't convert to boolean");
            }
            System.out.println("Some one use this name before:( please try another one");
        }
        player.setName(name);

    }

}
