package org.HO.Client.Role;

import org.HO.Logger.LogLevels;
import org.HO.Player;

import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

public class NormalMafia extends ClientWithRole {
    public NormalMafia(Player player) {
        super(player);
    }

    @Override
    public void start() {
        super.start();
        vote();
    }

    private void vote() {
        try {
            Scanner scanner = new Scanner(System.in);
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
