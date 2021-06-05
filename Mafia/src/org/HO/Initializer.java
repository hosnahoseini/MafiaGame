package org.HO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Initializer {
    private static Stack <PlayerRole> availableRoles = new Stack();
    private static Initializer instance = null;
    SharedData sharedData = SharedData.getInstance();


    private Initializer() {


        availableRoles.add(PlayerRole.GOD_FATHER);
        availableRoles.add(PlayerRole.DR_LECTER);

        availableRoles.add(PlayerRole.DR_CITY);
        availableRoles.add(PlayerRole.DETECTIVE);
        availableRoles.add(PlayerRole.PROFESSIONAL);
        availableRoles.add(PlayerRole.PSYCHOLOGIST);
        availableRoles.add(PlayerRole.DIE_HARD);
//        availableRoles.add(PlayerRole.MAYOR);

        for (int i = 0; i < sharedData.numberOfNormalMafias; i++)
            availableRoles.add(PlayerRole.NORMAL_MAFIA);

        for (int i = 0; i < sharedData.numberOfNormalPeople; i++)
            availableRoles.add(PlayerRole.NORMAL_PEOPLE);

        Collections.shuffle(availableRoles);
    }

    public static Initializer getInstance() {
        if (instance == null) {
            instance = new Initializer();
        }
        return instance;
    }

    public synchronized static PlayerRole assignRole(){
        return availableRoles.pop();
    }

}
