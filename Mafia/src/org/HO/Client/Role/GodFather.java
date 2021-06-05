package org.HO.Client.Role;

import org.HO.Player;
import org.HO.Poll;

import java.io.IOException;
import java.util.Scanner;

public class GodFather extends NormalMafia {
    public GodFather(Player player) {
        super(player);
    }

    @Override
    public void start(){
        super.start();
            waitUntilReceivingMsg("YOUR TURN");
            System.out.println(getPlayer().readTxt());
            String poll = getPlayer().readTxt();
            System.out.println(poll);
            Scanner scanner = new Scanner(System.in);
            getPlayer().writeTxt(scanner.next());

    }
}
