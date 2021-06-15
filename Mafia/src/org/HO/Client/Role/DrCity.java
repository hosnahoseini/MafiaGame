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

            System.out.println(getPlayer().readTxt());
            try {
                ArrayList<Player> citizen = (ArrayList<Player>) getPlayer().getInObj().readObject();
                for (Player player : citizen)
                    System.out.println("◉ " + player.getName());
                while (true) {
                    getInput(citizen);
                    getPlayer().writeTxtClient(vote);
                    String check = getPlayer().readTxt();
                    if (check.equals("thanks"))
                        break;
                    else
                        System.out.println(check);

                }
            } catch (IOException e) {
                System.err.println("some thing wrong in reading array list of choices from server");
            } catch (ClassNotFoundException e) {
                System.err.println("Can't convert to array list");
            }
        } else {
            System.out.println(start);
        }
    }
}
