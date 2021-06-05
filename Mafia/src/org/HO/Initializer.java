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


//        availableRoles.add(PlayerRole.GOD_FATHER);
//        sharedData.playerWithRoles.put(PlayerRole.GOD_FATHER, new ArrayList<>());
//        availableRoles.add(PlayerRole.DR_LECTER);
        //sharedData.playerWithRoles.put(PlayerRole.DR_LECTER, new ArrayList<>());

        availableRoles.add(PlayerRole.DR_CITY);
//        availableRoles.add(PlayerRole.DETECTIVE);
//        availableRoles.add(PlayerRole.PROFESSIONAL);
//        availableRoles.add(PlayerRole.PSYCHOLOGIST);
//        availableRoles.add(PlayerRole.DIE_HARD);
        availableRoles.add(PlayerRole.MAYOR);

        //sharedData.playerWithRoles.put(PlayerRole.NORMAL_MAFIA, new ArrayList<>());
        for (int i = 0; i < sharedData.numberOfNormalMafias; i++)
            availableRoles.add(PlayerRole.NORMAL_MAFIA);

        //sharedData.playerWithRoles.put(PlayerRole.NORMAL_PEOPLE, new ArrayList<>());
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
