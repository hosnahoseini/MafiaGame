package org.HO.Client.Role;

import org.HO.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Diehard extends ClientWithRole {
    public Diehard(Player player) {
        super(player);
    }

    @Override
    public void start() {
        super.start();
        System.out.println(getPlayer().readTxt());
        Scanner scanner = new Scanner(System.in);
        String result = scanner.nextLine();
        getPlayer().writeTxt(result);
        System.out.println(getPlayer().readTxt());
    }
}
