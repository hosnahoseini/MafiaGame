package org.HO.Client;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.PlayerRole;
import org.HO.Player;
import org.HO.Poll;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private Player player;
    private static final LoggingManager logger = new LoggingManager(Client.class.getName());
    private Scanner scanner = new Scanner(System.in);
    private ExecutorService pool = Executors.newCachedThreadPool();

    public void startClient(String ipAddress, int port) {
        try {
            Socket connection = new Socket(ipAddress, port);
            player = new Player(connection);

            System.out.println("connected to server");

            initializeInfo();

            waitUntilReceivingMsg("MORNING");

            startChat(connection);

            waitUntilReceivingMsg("POLL");

//            voteForMorningPoll();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void voteForMorningPoll() {
        try {
            logger.log("start poll " + player.getName(), LogLevels.INFO);
            Poll poll = (Poll) player.getInObj().readObject();
            logger.log("receive poll " + player.getName(), LogLevels.INFO);
            poll.showPoll();
            System.out.println("Enter your vote");
            int vote = scanner.nextInt();
            player.getOutObj().writeObject(vote);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startChat(Socket connection) {
        Thread write = new Thread(new WriteThread(connection, player));
        Thread read = new Thread(new ReadThread(connection, player));
        if (player.isAlive())
            write.start();
        read = new Thread(new ReadThread(connection, player));
        read.start();

        try {
            write.join();
            read.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    private void waitUntilReceivingMsg(String msg) throws IOException {
        while (true) {
            String input = player.getIn().readUTF();
            //System.out.println(input);
            if (input.equals(msg)) {
                System.out.println(msg);
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
