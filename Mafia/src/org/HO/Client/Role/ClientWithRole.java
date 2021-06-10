package org.HO.Client.Role;

import org.HO.Logger.LogLevels;
import org.HO.Player;

import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

public abstract class ClientWithRole {

    private Player player;

    public ClientWithRole(Player player) {
        this.player = player;
    }

    public void start() {
        waitUntilReceivingMsg("YOUR TURN");
    }


    public Player getPlayer() {
        return player;
    }

    public void waitUntilReceivingMsg(String msg) {
        while (true) {
            String input = null;
            try {
                input = player.getIn().readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (input.equals(msg)) {
                System.out.println(msg);
                break;
            }
        }
    }

    public boolean validInput(Collection<Player> choices, String name) {
        for(Player player:choices)
            if(player.getName().equalsIgnoreCase(name))
                return true;
        return false;
    }
}
