package org.HO.Client;

import org.HO.Color;
import org.HO.Player;


import java.util.Scanner;

/**
 * A class for client  to handle same method of getting input
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class ClientInputHandling {

    /**
     * check if input is exit and handle it
     * @param input input
     * @param player player
     * @return true if it is exit
     */
    public boolean checkIfInputIsExit(Player player, String input){
        if(input.equals("exit")) {
            player.writeTxtClient("exit");
            removePlayer(player);
            return true;
        }
        return false;
    }

    /**
     * remove player from client side
     * @param player player
     */
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

}
