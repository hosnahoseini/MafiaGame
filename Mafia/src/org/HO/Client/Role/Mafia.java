package org.HO.Client.Role;


import org.HO.Player;

import java.io.IOException;
import java.util.Collection;

/**
 * A class for client with mafia role
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class Mafia extends ClientWithRole {
    public Mafia(Player player) {
        super(player);
    }

    /**
     * all mafia start to vote
     */
    @Override
    public void start() {
        super.start();
        vote();
    }

    /**
     * vote for mafias poll
     */
    private void vote() {
        try {
            System.out.println(getPlayer().readTxt());
            Collection<Player> poll = (Collection<Player>) getPlayer().getInObj().readObject();
            for (Player player : poll)
                System.out.println(player);
            String vote;
            while (true) {
                System.out.println("Enter your vote");
                vote = getPlayer().writeWithExit(getPlayer());
                if(!validInput(poll, vote))
                    System.out.println("Invalid input");
                else
                    break;
            }
            System.out.println("thanks");
            getPlayer().writeTxt(vote);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
