package org.HO.Server;

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
            player.writeTxt("Who do you want to be killed?");
            player.writeTxt(poll.pollChoices());
            logger.log("write poll to " + player.getName(), LogLevels.INFO);
            String vote = (String) player.getInObj().readObject();
            poll.vote(vote, player);
            logger.log(player.getName() + " vote to " + vote,LogLevels.INFO);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        Thread.currentThread().interrupt();
    }
}
