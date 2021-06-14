package org.HO.Client.Role;

import org.HO.Player;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A class for client with dr lecter role
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class DrLecter extends Mafia {
    public DrLecter(Player player) {
        super(player);
    }

    /**
     * dr lecter heal one mafia if its possible
     */
    @Override
    public void start() {
        super.start();
        waitUntilReceivingMsg("YOUR TURN");
        String start = getPlayer().readTxt();
        if (start.equals("OK")) {

            System.out.println(getPlayer().readTxt());
            try {
                ArrayList<Player> mafias = (ArrayList<Player>) getPlayer().getInObj().readObject();
                for (Player player : mafias)
                    System.out.println(player.getName());
                getInput(mafias);
                while (true) {
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
