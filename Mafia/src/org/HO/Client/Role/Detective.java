package org.HO.Client.Role;

import org.HO.Player;

import java.io.IOException;
import java.util.ArrayList;
/**
 * A class for client with detective role
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class Detective extends ClientWithRole {
    public Detective(Player player) {
        super(player);
    }

    /**
     * detective start to guess
     */
    @Override
    public void start() {
        super.start();
        System.out.println(getPlayer().readTxt());
        try {
            ArrayList<Player> players = (ArrayList<Player>) getPlayer().getInObj().readObject();
            for (Player player : players)
                System.out.println(player.getName());
            String name;
            while (true) {
                name = getPlayer().writeWithExit(getPlayer());
                if (!validInput(players, name))
                    System.out.println("Invalid input, try again");
                else
                    break;
            }
            getPlayer().writeTxt(name);
            System.out.println(getPlayer().readTxt());
        } catch (IOException e) {
            System.err.println("some thing wrong in reading array list of choices from server");
        } catch (ClassNotFoundException e) {
            System.err.println("Can't convert to array list");
        }
    }
}


