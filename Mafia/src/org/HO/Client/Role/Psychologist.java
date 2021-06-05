package org.HO.Client.Role;

import org.HO.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Psychologist extends ClientWithRole {
    public Psychologist(Player player) {
        super(player);
    }

    @Override
    public void start() {
        super.start();
        System.out.println(getPlayer().readTxt());
        Scanner scanner = new Scanner(System.in);
        String result = scanner.nextLine();
        getPlayer().writeTxt(result);
        if(result.equals("y"))
            try {
                System.out.println(getPlayer().readTxt());
                ArrayList<Player> players = (ArrayList<Player>) getPlayer().getInObj().readObject();
                for (Player player : players)
                    System.out.println(player.getName());
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
