package org.HO.Client.Role;

import org.HO.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Professional extends ClientWithRole {
    public Professional(Player player) {
        super(player);
    }

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
            String name ;
            while (true) {
                name = getPlayer().writeWithExit(getPlayer());
                if (!validInput(players, name))
                    System.out.println("Invalid input, try again");
                else
                    break;
            }
            getPlayer().writeTxt(name);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
