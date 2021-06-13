package org.HO.Client.Role;

import org.HO.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A class for client considering it role to do his own tasks
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public abstract class ClientWithRole {

    private Player player;
    protected boolean running = true;
    protected String vote;
    protected ClientInputHandling clientInputHandling = new ClientInputHandling();
    public ClientWithRole(Player player) {
        this.player = player;
    }

    /**
     * start main task which is different for each role
     */
    public void start() {
        waitUntilReceivingMsg("YOUR TURN");
    }

    /**
     * get player
     *
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * receive any thing from server until getting a special message
     *
     * @param msg special message
     */
    public void waitUntilReceivingMsg(String msg) {
        while (true) {
            String input = null;
            input = player.readTxt();
            if (input.equals(msg) && input!=null) {
                System.out.println(msg);
                break;
            }
        }
    }

    /**
     * check if an input of client is in the choices that he has
     *
     * @param choices valid choices
     * @param name    client choice
     * @return true if his choice is valid
     */
//    public boolean validInput(Collection<Player> choices, String name) {
//        for (Player player : choices)
//            if (player.getName().equalsIgnoreCase(name))
//                return true;
//        return false;
//    }
//
//    public void getInput(Collection<Player> poll) {
//        vote = "";
//        running = true;
//        Timer timer = new Timer();
//        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                running = false;
//                System.out.println("time ended");
//                timer.cancel();
//            }
//        };
//
//        timer.schedule(task, 10000);
//        try {
//            while (running) {
//                while (!scanner.ready()) {
//                    Thread.sleep(50);
//                    if (!running)
//                        return;
//                }
//                vote = scanner.readLine();
//                if(player.checkIfInputIsExit(vote))
//                    timer.cancel();
//                if (!validInput(poll, vote)) {
//                    System.out.println("Invalid input!Try again");
//                    vote = "";
//                }else {
//                    System.out.println("thanks");
//                    timer.cancel();
//                    break;
//                }
//
//            }
//
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void getYesOrNoInput(){
//        running = true;
//        Timer timer = new Timer();
//        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                running = false;
//                System.out.println("time ended");
//                timer.cancel();
//            }
//        };
//
//        timer.schedule(task, 5000);
//        try {
//            while (running) {
//                while (!scanner.ready()) {
//                    Thread.sleep(50);
//                    if (!running) {
//                        return;
//                    }
//                }
//                vote = scanner.readLine();
//                if(player.checkIfInputIsExit(vote))
//                    timer.cancel();
//                System.out.println("thanks");
//                timer.cancel();
//                break;
//            }
//
//
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}
