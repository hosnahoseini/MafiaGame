package org.HO.Client;

import org.HO.Client.Role.ClientFactory;
import org.HO.Client.Role.ClientWithRole;
import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.PlayerRole;
import org.HO.Player;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private Player player;
    private static final LoggingManager logger = new LoggingManager(Client.class.getName());
    private Scanner scanner = new Scanner(System.in);
    private ClientWithRole clientWithRole;
    private ClientFactory factory = new ClientFactory();

    public void startClient(String ipAddress, int port) {
        try {
            Socket connection = new Socket(ipAddress, port);
            player = new Player(connection);

            System.out.println("connected to server");

            initializeInfo();


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


            if (!player.isAlive())
                while (true) {
                    System.out.println(player.readTxt());
                }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void showPollResult() {
        String poll = player.readTxt();
        logger.log("read poll res", LogLevels.INFO);
        System.out.println("The result is:");
        System.out.println(poll);

    }

    private void voteForMorningPoll() {
        ExecutorService pool = Executors.newCachedThreadPool();
        try {
            logger.log("start poll " + player.getName(), LogLevels.INFO);
            System.out.println(player.readTxt());
            Collection <Player> poll=(Collection<Player>) player.getInObj().readObject();
            logger.log("receive poll " + player.getName(), LogLevels.INFO);
            for (Player player:poll)
                System.out.println(player);
            final String[] vote = new String[1];
            while (true) {
                System.out.println("Enter your vote");
                vote[0] = player.writeWithExit(player);
                if (vote[0].equals(player.getName()))
                    System.out.println("You can't vote to your self try another player");
                else if(!validInput(poll, vote[0]))
                    System.out.println("Invalid input");
                else
                    break;

            }


//            pool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    while (true) {
//                        System.out.println("Enter your vote");
//                        vote[0] = scanner.next();
//                        if (vote[0].equals(player.getName()))
//                            System.out.println("You can't vote to your self try another player");
//                        else
//                            break;
//                    }
//                }
//            });
//            pool.shutdown();
//            pool.awaitTermination(2000, TimeUnit.MILLISECONDS);

            System.out.println("thanks");
            player.writeTxt(vote[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private boolean validInput(Collection<Player> choices, String name) {
        for(Player player:choices)
            if(player.getName().equalsIgnoreCase(name))
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


    private void ReceiveUntilGetMsg(String msg) throws IOException {
        while (true) {
            String input = player.getIn().readUTF();
            logger.log(player.getName() + " read " + input, LogLevels.INFO);
            System.out.println("->" + input);
            if (input.equals(msg)) {
                break;
            }
            receiverKilledMessage(input);
            receiverMuteMessage(input);
        }
    }

    private void receiverMuteMessage(String input) {
        if (input.contains(player.getName() + " is muted"))
                player.setMute(true);
            else
                player.setMute(false);
    }

    private void receiverKilledMessage(String input) throws IOException {
        if (input.contains("You've been killed:(")) {
            System.out.println(player.readTxt());
            String result = scanner.next();
            player.writeTxt(result);
            player.setAlive(false);
            if (result.equals("n")) {
                System.out.println("BYE");
                player.getConnection().close();
                System.exit(0);
            }
        }
    }

    private void waitUntilRecivingMsg(String msg) throws IOException {
        while (true) {
            String input = player.getIn().readUTF();
            if (input.equals(msg)) {
                logger.log("read" + msg, LogLevels.INFO);
                break;
            }
        }
    }

    private void initializeInfo() throws IOException, ClassNotFoundException {
        setName();
        player.setRole((PlayerRole) player.getInObj().readObject());
        System.out.println("Welcome to our game \nyour role is -> " + player.getRole());
        System.out.println("type GO whenever you are ready to play");
        //scanner.next();
        player.getOutObj().writeObject(true);
        System.out.println("FIRST MORNING");
        clientWithRole = factory.getClient(player);

    }

    private void setName() throws IOException, ClassNotFoundException {
        System.out.println("Enter your name: ");
        String name;
        while (true) {
            Random random = new Random();
            name = scanner.nextLine();
//            name = String.valueOf((char)(random.nextInt(26) + 64));
            player.writeTxt(name);
            if ((boolean) player.getInObj().readObject())
                break;
            System.out.println("Some one use this name before:( please try another one");
        }
        player.setName(name);

    }

}
