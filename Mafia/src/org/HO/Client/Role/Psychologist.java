package org.HO.Client.Role;

import org.HO.Player;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A class for client with Psychologist role
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class Psychologist extends ClientWithRole {
    public Psychologist(Player player) {
        super(player);
    }

    /**
     * Psychologist decide if he wants to mute s.o. for next turn
     */
    @Override
    public void start() {
        super.start();
        System.out.println(getPlayer().readTxt());
        String result = getPlayer().writeWithExit(getPlayer());
        getPlayer().writeTxt(result);
        if(result.equals("y"))
            try {
                System.out.println(getPlayer().readTxt());
                ArrayList<Player> players = (ArrayList<Player>) getPlayer().getInObj().readObject();
                for (Player player : players)
                    System.out.println(player.getName());
                getInput(players);
                getPlayer().writeTxt(vote);
            } catch (IOException e) {
                System.err.println("some thing wrong in reading array list of choices from server");
            } catch (ClassNotFoundException e) {
                System.err.println("Can't convert to array list");
            }
    }
}
