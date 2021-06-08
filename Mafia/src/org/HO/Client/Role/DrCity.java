package org.HO.Client.Role;

import org.HO.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class DrCity extends ClientWithRole {
    public DrCity(Player player) {
        super(player);
    }

    @Override
    public void start() {
        super.start();
        while (true) {
            System.out.println(getPlayer().readTxt());
            try {
                ArrayList<Player> people = (ArrayList<Player>) getPlayer().getInObj().readObject();
                for (Player player : people)
                    System.out.println(player.getName());
                Scanner scanner = new Scanner(System.in);
                String name = readWithExit(getPlayer());
                getPlayer().writeTxt(name);

                String check = getPlayer().readTxt();
                System.out.println(check);
                if (check.equals("thanks"))
                    break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
