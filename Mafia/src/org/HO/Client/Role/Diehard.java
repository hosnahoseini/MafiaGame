package org.HO.Client.Role;

import org.HO.Player;

import java.util.Scanner;

public class Diehard extends ClientWithRole {
    private static int n = 0;
    public Diehard(Player player) {
        super(player);
    }

    @Override
    public void start() {
        super.start();
        System.out.println(getPlayer().readTxt());;
        String result = writeWithExit(getPlayer());
        if(n <= 1 && result.equals("y"))
            getPlayer().writeTxt(result);
        else{
            System.out.println("you can't inquire any more");
            getPlayer().writeTxt("n");
        }

        System.out.println(getPlayer().readTxt());
    }
}
