package org.HO.Client;

import org.HO.Client.Role.ClientFactory;
import org.HO.Client.Role.ClientWithRole;
import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.PlayerRole;
import org.HO.Player;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
                System.out.println(player.readTxt());
            }

    }

    /**
     * main loop of game
     */
    private void gameLoop() {
        do {
            ReceiveUntilGetMsg("CHAT TIME");

            startChat();

            waitUntilRecivingMsg("POLL");

            voteForMorningPoll();

            waitUntilRecivingMsg("VOTING TIME ENDED");

            showPollResult();

            if (player.getRole() == PlayerRole.MAYOR)
                clientWithRole.start();

            ReceiveUntilGetMsg("NIGHT");

            if (!player.isAlive())
                break;

            if (player.getRole() != PlayerRole.MAYOR)
                clientWithRole.start();

            ReceiveUntilGetMsg("MORNING");

            if (!player.isAlive())
                break;

        } while (true);
    }

    private void showPollResult() {
        String poll = player.readTxt();
        logger.log("read poll res", LogLevels.INFO);
        System.out.println("The result is:");
        System.out.println(System.currentTimeMillis() / 1000);
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
            final String[] vote = new String[1];
            while (true) {
                System.out.println("Enter your vote");
                vote[0] = player.writeWithExit(player);
                if (vote[0].equals(player.getName()))
                    System.out.println("You can't vote to your self try another player");
                else if (!validInput(poll, vote[0]))
                    System.out.println("Invalid input");
                else
                    break;

            }
            System.out.println("thanks");
            player.writeTxt(vote[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
//    private void voteForMorningPoll() {
//        try {
//            logger.log("start poll " + player.getName(), LogLevels.INFO);
//
//            System.out.println(player.readTxt());
//            Collection<Player> poll = (Collection<Player>) player.getInObj().readObject();
//            logger.log("receive poll " + player.getName(), LogLevels.INFO);
//            for (Player player : poll)
//                System.out.println(player);
//
//            vote(poll);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void vote(Collection<Player> poll) {
//        running = true;
//        Timer timer = new Timer();
//        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                running = false;
//                if (validInput(poll, vote)) {
//                    player.writeTxt(vote);
//                    System.out.println(vote);
//                }
//                else
//                    player.writeTxt("");
//                timer.cancel();
//            }
//        };
//
//        timer.schedule(task, 20000);
//        try {
//            while (running) {
//                while (!scanner.ready()) {
//                    Thread.sleep(50);
//                    if (!running)
//                        return;
//                }
//                vote = scanner.readLine();
//                if (!validInput(poll, vote)) {
//                    System.out.println("Invalid input!Try again");
//                    vote = "";
//                }else
//                    System.out.println("thanks");
//
//            }
//
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

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

        previousChat();

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

    private void previousChat() {
        System.out.println("Do you want to see previous chats?(y/n)");
        String result = scanner.next();
        player.writeTxt(result);
        if (result.equals("y")) {
            ArrayList<String> currentChats = new ArrayList<>();
            String chat;
            while (!(chat = player.readTxt()).contains("--CHAT BOX--"))
                currentChats.add(chat);
            System.out.println(chat);
            System.out.println("----END!----");
            for (String s : currentChats)
                System.out.println(s);
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
            logger.log(player.getName() + " read " + input, LogLevels.INFO);
            if(msg == "MORNING") {
                System.out.println("█▄ ▄█ ▄▀▀▄ █▀▄ █▄ █ █ █▄ █ ▄▀▀\n" +
                        "█ ▀ █ █  █ ██▀ █ ▀█ █ █ ▀█ █ ▀█\n" +
                        "▀   ▀  ▀▀  ▀ ▀ ▀  ▀ ▀ ▀  ▀ ▀▀▀▀");
            }
            else if(msg == "NIGHT") {
                System.out.println("     ██▀██▀█▀█▀▀██▀█▀█▀▀▀█\n" +
                        "     ██─▄▀─█─█─█▀█─▄─██─██\n" +
                        "     ██▄██▄█▄█▄▄▄█▄█▄██▄██ ");
            }
            else System.out.println("->" + input);
            if (input.equals(msg)) {
                break;
            }
            receiverKilledMessage(input);
            receiverMuteMessage(input);
            receiverWinner(input);
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
            player.writeTxt(result);
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
                System.out.println(msg + System.currentTimeMillis() / 1000);
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
            player.writeTxt(name);
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
