package org.HO.Server;

import org.HO.Client.Client;
import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;
import org.HO.Poll;

import java.io.IOException;

public class PollHandler implements Runnable{

    Poll poll;
    private Player player;
    private static final LoggingManager logger = new LoggingManager(PollHandler.class.getName());

    public PollHandler(Poll poll, Player player) {
        this.poll = poll;
        this.player = player;
    }

    @Override
    public void run() {
        try {
            player.getOutObj().writeObject(poll);
            logger.log("write poll to " + player.getName(), LogLevels.INFO);
            String vote = (String) player.getInObj().readObject();
            logger.log(player.getName() + " vote to " + vote,LogLevels.INFO);
            poll.vote(vote, player);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        Thread.currentThread().interrupt();
    }
}