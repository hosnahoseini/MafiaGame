package org.HO.Client.Role;

import org.HO.Player;

import java.io.IOException;
import java.util.Collection;

/**
 * A class for client considering it role to do his own tasks
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public abstract class ClientWithRole {

    private Player player;

    public ClientWithRole(Player player) {
        this.player = player;
    }

    /**
     * start main task which is different for each role
     */
    public void start() {
        waitUntilReceivingMsg("YOUR TURN");
    }

    /**
     * get player
     *
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * receive any thing from server until getting a special message
     *
     * @param msg special message
     */
    public void waitUntilReceivingMsg(String msg) {
        while (true) {
            String input = null;
            input = player.readTxt();
            if (input.equals(msg)) {
                System.out.println(msg);
                break;
            }
        }
    }

    /**
     * check if an input of client is in the choices that he has
     *
     * @param choices valid choices
     * @param name    client choice
     * @return true if his choice is valid
     */
    public boolean validInput(Collection<Player> choices, String name) {
        for (Player player : choices)
            if (player.getName().equalsIgnoreCase(name))
                return true;
        return false;
    }
}
