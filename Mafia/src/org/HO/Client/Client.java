package org.HO.Client;

import org.HO.Logger.LoggingManager;
import org.HO.PlayerRole;
import org.HO.Player;
import org.HO.Poll;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private Player player;
    private static final LoggingManager logger = new LoggingManager(Client.class.getName());
    Scanner scanner = new Scanner(System.in);

    public void startClient(String ipAddress, int port) {
        try  {
            Socket connection = new Socket(ipAddress, port);
            player = new Player(connection);

            System.out.println("connected to server");

            initializeInfo();

            waitUntilMorning();

            startChat(connection);

            voteForMorningPoll();


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
            Poll poll = (Poll) player.getInObj().readObject();
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
        if(player.isAlive())
            new Thread(new WriteThread(connection, player.getName())).start();
        new Thread(new ReadThread(connection, player.getName())).start();
    }


    private void waitUntilMorning() throws IOException {
        while (true) {
            String input = player.getIn().readUTF();
            System.out.println(input);
            if (input.equals("MORNING"))
                break;
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
