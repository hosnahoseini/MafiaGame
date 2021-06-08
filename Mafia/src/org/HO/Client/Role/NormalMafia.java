package org.HO.Client.Role;

import org.HO.Player;
import org.HO.Poll;

import java.io.IOException;
import java.util.Scanner;

public class NormalMafia extends ClientWithRole{
    public NormalMafia(Player player) {
        super(player);
    }
    @Override
    public void start(){
        super.start();
        vote();
    }

    private void vote() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println(getPlayer().readTxt());
            String poll = getPlayer().readTxt();
            System.out.println(poll);
            System.out.println("Enter your vote");
            String vote = readWithExit(getPlayer());
            System.out.println("thanks");
            getPlayer().getOutObj().writeObject(vote);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
