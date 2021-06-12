package org.HO.Client.Role;

import org.HO.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A class for client with mayor role
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class Mayor extends ClientWithRole {
    public Mayor(Player player) {
        super(player);
    }

    /**
     * mayor decide if he wants to cancel morning poll result or no
     */
    @Override
    public void start() {
        System.out.println(getPlayer().readTxt());
        System.out.println(getPlayer().readTxt());
        vote = "y";
        getYesOrNoInput();

    }


}
