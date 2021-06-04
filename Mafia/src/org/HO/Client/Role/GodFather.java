package org.HO.Client.Role;

import org.HO.Player;
import org.HO.Poll;

import java.io.IOException;
import java.util.Scanner;

public class GodFather extends ClientWithRole {
    public GodFather(Player player) {
        super(player);
    }

    @Override
    public void start(){
        try {
            System.out.println(getPlayer().readTxt());
            Poll poll = (Poll) getPlayer().getInObj().readObject();
            System.out.println(poll);
            Scanner scanner = new Scanner(System.in);
            getPlayer().writeTxt(scanner.next());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
