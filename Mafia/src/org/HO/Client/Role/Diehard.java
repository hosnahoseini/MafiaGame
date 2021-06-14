package org.HO.Client.Role;

import org.HO.Player;

/**
 * A class for client with die hard role
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class Diehard extends ClientWithRole {

    public Diehard(Player player) {
        super(player);
    }

    /**
     * die hard decide to request for inquire or not
     */
    @Override
    public void start() {
        super.start();
        System.out.println(getPlayer().readTxt());
        vote = "n";
        getYesOrNoInput();

            getPlayer().writeTxtClient(vote);


        System.out.println(getPlayer().readTxt());
    }
}
