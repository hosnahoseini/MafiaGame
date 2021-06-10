package org.HO.Client.Role;

import org.HO.Logger.LogLevels;
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
        String start = getPlayer().readTxt();
        if (start.equals("OK")) {
            while (true) {
                System.out.println(getPlayer().readTxt());
                try {
                    ArrayList<Player> citizen = (ArrayList<Player>) getPlayer().getInObj().readObject();
                    for (Player player : citizen)
                        System.out.println(player.getName());
                    String name;
                    while (true) {
                        name = writeWithExit(getPlayer());
                        if (!validInput(citizen, name))
                            System.out.println("Invalid input, try again");
                        else
                            break;
                    }
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
        }else {
            System.out.println(start);
        }
    }
}
