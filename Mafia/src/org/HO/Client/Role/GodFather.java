package org.HO.Client.Role;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;


import java.io.IOException;
import java.util.Collection;

/**
 * A class for client with godfather role
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class GodFather extends Mafia {
    private static final LoggingManager logger = new LoggingManager(GodFather.class.getName());

    public GodFather(Player player) {
        super(player);
    }

    /**
     * godfather decide which citizen is going to be killed
     */
    @Override
    public void start() {
        super.start();
        waitUntilReceivingMsg("YOUR TURN");
        System.out.println(getPlayer().readTxt());

        try {
            Collection <Player> poll = (Collection<Player>) getPlayer().getInObj().readObject();
            String pollResult = getPlayer().readTxt();
            System.out.println(pollResult);
            getInput(poll);

            getPlayer().writeTxtClient(vote);
            logger.log(vote, LogLevels.INFO);
        } catch (IOException e) {
            System.err.println("some thing wrong in reading array list of choices from server");
        } catch (ClassNotFoundException e) {
            System.err.println("Can't convert to collection");
        }
    }
}
