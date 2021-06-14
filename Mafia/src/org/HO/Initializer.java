package org.HO;

import java.util.Stack;

/**
 * A class to initialize roles(singleton design pattern)
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */

public class Initializer {
    private static Stack<PlayerRole> availableRoles = new Stack();
    private static Initializer instance = null;
    SharedData sharedData = SharedData.getInstance();


    /**
     * initialize role according to number of players
     *
     * @param numberOfPlayers to number of players
     */
    private Initializer(int numberOfPlayers) {

        if (numberOfPlayers >= 8) {
            availableRoles.add(PlayerRole.GOD_FATHER);
            availableRoles.add(PlayerRole.DR_LECTER);
            availableRoles.add(PlayerRole.DR_CITY);
            availableRoles.add(PlayerRole.DETECTIVE);
            availableRoles.add(PlayerRole.PROFESSIONAL);
            availableRoles.add(PlayerRole.PSYCHOLOGIST);
            availableRoles.add(PlayerRole.DIE_HARD);
            availableRoles.add(PlayerRole.MAYOR);

            for (int i = 0; i < sharedData.numberOfNormalMafias; i++)
                availableRoles.add(PlayerRole.NORMAL_MAFIA);

            for (int i = 0; i < sharedData.numberOfNormalPeople; i++)
                availableRoles.add(PlayerRole.NORMAL_PEOPLE);
        } else {
            switch (numberOfPlayers) {
                case 7:
                    availableRoles.add(PlayerRole.GOD_FATHER);
                    availableRoles.add(PlayerRole.DR_LECTER);
                    availableRoles.add(PlayerRole.DR_CITY);
                    availableRoles.add(PlayerRole.DETECTIVE);
                    availableRoles.add(PlayerRole.PROFESSIONAL);
                    availableRoles.add(PlayerRole.DIE_HARD);
                    availableRoles.add(PlayerRole.MAYOR);
                    break;
                case 6:
                    availableRoles.add(PlayerRole.GOD_FATHER);
                    availableRoles.add(PlayerRole.DR_LECTER);
                    availableRoles.add(PlayerRole.DR_CITY);
                    availableRoles.add(PlayerRole.DETECTIVE);
                    availableRoles.add(PlayerRole.PROFESSIONAL);
                    availableRoles.add(PlayerRole.MAYOR);
                case 5:
                    availableRoles.add(PlayerRole.GOD_FATHER);
                    availableRoles.add(PlayerRole.DR_LECTER);
                    availableRoles.add(PlayerRole.DR_CITY);
                    availableRoles.add(PlayerRole.DETECTIVE);
                    availableRoles.add(PlayerRole.MAYOR);
                case 3:
//                    availableRoles.add(PlayerRole.DR_CITY);
                    availableRoles.add(PlayerRole.GOD_FATHER);
                    availableRoles.add(PlayerRole.DIE_HARD);
                    availableRoles.add(PlayerRole.MAYOR);
//                    availableRoles.add(PlayerRole.NORMAL_PEOPLE);
            }


        }
//        Collections.shuffle(availableRoles);
    }

    /**
     * get instance of initializer
     *
     * @param numberOfPlayers numberOfPlayers
     * @return instance of initializer
     */
    public static Initializer getInstance(int numberOfPlayers) {
        if (instance == null) {
            instance = new Initializer(numberOfPlayers);
        }
        return instance;
    }

    /**
     * give a role
     *
     * @return role
     */
    public synchronized static PlayerRole assignRole() {
        return availableRoles.pop();
    }

}
