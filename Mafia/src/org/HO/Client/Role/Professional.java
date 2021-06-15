package org.HO.Client.Role;

import org.HO.Player;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A class for client with Professional role
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class Professional extends ClientWithRole {
    public Professional(Player player) {
        super(player);
    }

    /**
     * Professional start to kill s.o. if he wants
     */
    @Override
    public void start() {
        super.start();
        System.out.println(getPlayer().readTxt());
        vote = "n";
        getYesOrNoInput();
        getPlayer().writeTxtClient(vote);
        if(vote.equals("y"))
        try {
            System.out.println(getPlayer().readTxt());
            ArrayList<Player> players = (ArrayList<Player>) getPlayer().getInObj().readObject();
            for (Player player : players)
                System.out.println("◉ " + player.getName());
            getInput(players);
            getPlayer().writeTxtClient(vote);
        } catch (IOException e) {
            System.err.println("some thing wrong in reading array list of choices from server");
        } catch (ClassNotFoundException e) {
            System.err.println("Can't convert to array list");
        }
    }
}
