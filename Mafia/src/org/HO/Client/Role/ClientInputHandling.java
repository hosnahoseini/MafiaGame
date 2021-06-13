package org.HO.Client.Role;

import org.HO.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class ClientInputHandling {

    /**
     * check if input is exit and handle it
     * @param input input
     */
    public boolean checkIfInputIsExit(Player player, String input){
        if(input.equals("exit")) {
            player.writeTxtClient("exit");
            System.out.println("you enter exit");
            removePlayer(player);
            return true;
        }
        return false;
    }

    public void removePlayer(Player player) {
        System.out.println(player.readTxt());
        System.out.println(player.readTxt());
        Scanner scanner = new Scanner(System.in);
        String result = scanner.next();
        player.writeTxtClient(result);
        if (result.equals("n")) {
            player.close();
            System.exit(5);
        }else {
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
    }
//
//    /**
//     * check if an input of client is in the choices that he has
//     *
//     * @param choices valid choices
//     * @param name    client choice
//     * @return true if his choice is valid
//     */
//    public boolean validInput(Collection<Player> choices, String name) {
//        for (Player player : choices)
//            if (player.getName().equalsIgnoreCase(name))
//                return true;
//        return false;
//    }
//
//    public String getInputForVote(Collection<Player> poll, Player player) {
//        String vote = "";
//        final boolean[] running = {true};
//        Timer timer = new Timer();
//        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                running[0] = false;
//                System.out.println("time ended");
//                timer.cancel();
//            }
//        };
//
//        timer.schedule(task, 20000);
//        try {
//            while (running[0]) {
//                while (!scanner.ready()) {
//                    Thread.sleep(50);
//                    if (!running[0])
//                        return vote;
//                }
//                vote = scanner.readLine();
//                if(checkIfInputIsExit(player, vote))
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
//        return vote;
//    }
//
//    public void getYesOrNoInput(String vote, Player player){
//        final boolean[] running = {true};
//        Timer timer = new Timer();
//        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                running[0] = false;
//                System.out.println("time ended");
//                timer.cancel();
//            }
//        };
//
//        timer.schedule(task, 5000);
//        try {
//            while (running[0]) {
//                while (!scanner.ready()) {
//                    Thread.sleep(50);
//                    if (!running[0]) {
//                        return;
//                    }
//                }
//                vote = scanner.readLine();
//                if(checkIfInputIsExit(player, vote))
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
