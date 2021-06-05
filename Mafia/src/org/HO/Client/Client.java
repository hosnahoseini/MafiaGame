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
import java.util.Scanner;

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

            ReceiveUntilGetMsg("MORNING");

            startChat(connection);

            waitUntilRecivingMsg("POLL");

            voteForMorningPoll();

            waitUntilRecivingMsg("VOTING TIME ENDED");

            showPollResult();

            clientWithRole.start();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void showPollResult() {
        try {
            String poll = (String) player.getInObj().readObject();
            logger.log("read poll res",LogLevels.INFO);
            System.out.println("The result is:");
            System.out.println(poll);
            Thread.sleep(5000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void voteForMorningPoll() {
        try {
            logger.log("start poll " + player.getName(), LogLevels.INFO);
            String poll = player.readTxt();
            logger.log("receive poll " + player.getName(), LogLevels.INFO);
            System.out.println(poll);
            System.out.println("Enter your vote");
            String vote = scanner.next();
            System.out.println("thanks");
            player.getOutObj().writeObject(vote);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startChat(Socket connection) {


        Thread read = new Thread(new ReadThread(connection, player));
        Thread write = new WriteThread(connection, player);
        if (player.isAlive())
            write.start();
        read.start();

        try {
            read.join();
            write.interrupt();

            System.out.println(write.isInterrupted());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private void ReceiveUntilGetMsg(String msg) throws IOException {
        while (true) {
            String input = player.getIn().readUTF();
            System.out.println("->" + input);
            if (input.equals(msg)) {
                logger.log("read" + msg ,LogLevels.INFO);
                break;
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
        clientWithRole = factory.getClient(player);

    }

    private void setName() throws IOException, ClassNotFoundException {
        System.out.println("Enter your name: ");
        String name;
        while (true) {
            name = scanner.next();
            player.getOutObj().writeObject(name);
            if ((boolean) player.getInObj().readObject())
                break;
            System.out.println("Some one use this name before:( please try another one");
        }
        player.setName(name);

    }

}
