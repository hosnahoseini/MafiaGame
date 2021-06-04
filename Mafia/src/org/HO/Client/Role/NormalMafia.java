package org.HO.Client.Role;

import org.HO.Logger.LogLevels;
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
            Poll poll = (Poll) getPlayer().getInObj().readObject();
            poll.showPoll();
            System.out.println("Enter your vote");
            String vote = scanner.next();
            System.out.println("thanks");
            getPlayer().getOutObj().writeObject(vote);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
