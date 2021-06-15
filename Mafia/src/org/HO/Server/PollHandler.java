package org.HO.Server;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;
import org.HO.Poll;
import org.HO.SharedData;

import java.io.EOFException;
import java.io.IOException;

/**
 * A class for handling poll
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class PollHandler implements Runnable {

    Poll poll;
    private Player player;
    private static final LoggingManager logger = new LoggingManager(PollHandler.class.getName());
    private SharedData sharedData = SharedData.getInstance();
    private ServerOutputHandling serverOutputHandling = new ServerOutputHandling();

    public PollHandler(Poll poll, Player player) {
        this.poll = poll;
        this.player = player;
    }

    @Override
    public void run() {
        try {

            player.writeTxt("Who do you want to be killed?");
            player.getOutObj().writeObject(poll.getPoll().keySet());
            logger.log("write poll to " + player.getName(), LogLevels.INFO);
            String vote = serverOutputHandling.readWithExit(player);

            poll.vote(vote, player);
        } catch (IOException e) {
            player.close();
            sharedData.players.remove(player);

        }
        Thread.currentThread().interrupt();
    }

}
