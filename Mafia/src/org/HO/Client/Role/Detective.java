package org.HO.Client.Role;

import org.HO.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Detective extends ClientWithRole {
    public Detective(Player player) {
        super(player);
    }

    @Override
    public void start() {
        super.start();
        System.out.println(getPlayer().readTxt());
        try {
            ArrayList<Player> players = (ArrayList<Player>) getPlayer().getInObj().readObject();
            for (Player player : players)
                System.out.println(player.getName());
            Scanner scanner = new Scanner(System.in);
            String name = scanner.nextLine();
            getPlayer().writeTxt(name);
            System.out.println(getPlayer().readTxt());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}


