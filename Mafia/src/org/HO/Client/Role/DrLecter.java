package org.HO.Client.Role;

import org.HO.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class DrLecter extends NormalMafia {
    public DrLecter(Player player) {
        super(player);
    }

    @Override
    public void start() {
        super.start();
        waitUntilReceivingMsg("YOUR TURN");
        while (true) {
            System.out.println(getPlayer().readTxt());
            try {
                ArrayList<Player> mafias = (ArrayList<Player>) getPlayer().getInObj().readObject();
                for (Player player : mafias)
                    System.out.println(player.getName());
                Scanner scanner = new Scanner(System.in);
                String name = scanner.nextLine();
                getPlayer().writeTxt(name);
                String check = getPlayer().readTxt();
                System.out.println(check);
                if (check == "thanks")
                    break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
