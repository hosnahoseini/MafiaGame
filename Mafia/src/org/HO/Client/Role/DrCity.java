package org.HO.Client.Role;

import org.HO.Player;

import java.io.IOException;
import java.util.ArrayList;
/**
 * A class for client with dr city role
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */

public class DrCity extends ClientWithRole {
    public DrCity(Player player) {
        super(player);
    }

    /**
     * dr city heal one citizen if its possible
     */
    @Override
    public void start() {
        super.start();
        String start = getPlayer().readTxt();
        if (start.equals("OK")) {
            while (true) {
                System.out.println(getPlayer().readTxt());
                try {
                    ArrayList<Player> citizen = (ArrayList<Player>) getPlayer().getInObj().readObject();
                    for (Player player : citizen)
                        System.out.println(player.getName());
                    String name;
                    while (true) {
                        name = getPlayer().writeWithExit(getPlayer());
                        if (!validInput(citizen, name))
                            System.out.println("Invalid input, try again");
                        else
                            break;
                    }
                    getPlayer().writeTxt(name);
                    String check = getPlayer().readTxt();
                    System.out.println(check);
                    if (check.equals("thanks"))
                        break;
                } catch (IOException e) {
                    System.err.println("some thing wrong in reading array list of choices from server");
                } catch (ClassNotFoundException e) {
                    System.err.println("Can't convert to array list");
                }
            }
        }else {
            System.out.println(start);
        }
    }
}
