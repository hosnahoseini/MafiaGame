package org.HO.Client.Role;

import org.HO.Player;

/**
 * A class for client with die hard role
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class Diehard extends ClientWithRole {
//    private static int n = 0;
    //TODO:remove n
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
//        if(n <= 1 && vote.equals("y"))
            getPlayer().writeTxt(vote);
//        else{
//            System.out.println("you can't inquire any more");
//            getPlayer().writeTxt("n");
//        }

        System.out.println(getPlayer().readTxt());
    }
}
