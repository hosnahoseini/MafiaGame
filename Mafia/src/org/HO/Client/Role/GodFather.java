package org.HO.Client.Role;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;
import org.HO.Poll;

import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

public class GodFather extends NormalMafia {
    private static final LoggingManager logger = new LoggingManager(GodFather.class.getName());

    public GodFather(Player player) {
        super(player);
    }

    @Override
    public void start() {
        super.start();
        waitUntilReceivingMsg("YOUR TURN");
        System.out.println(getPlayer().readTxt());

        try {
            Collection <Player> poll = (Collection<Player>) getPlayer().getInObj().readObject();
            String pollResult = getPlayer().readTxt();
            System.out.println(pollResult);
            String result;
            while (true) {
                result = getPlayer().writeWithExit(getPlayer());
                if (!validInput(poll, result))
                    System.out.println("Invalid input, try again");
                else
                    break;
            }
            getPlayer().writeTxt(result);
            logger.log(result, LogLevels.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
