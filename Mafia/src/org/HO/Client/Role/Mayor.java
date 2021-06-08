package org.HO.Client.Role;

import org.HO.Player;

import java.io.IOException;
import java.util.Scanner;

public class Mayor extends ClientWithRole {
    public Mayor(Player player) {
        super(player);
    }

    @Override
    public void start() {
        System.out.println(getPlayer().readTxt());
        System.out.println(getPlayer().readTxt());
        Scanner scanner = new Scanner(System.in);
        getPlayer().writeTxt(readWithExit(getPlayer()));
        
    }


}
