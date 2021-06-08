package org.HO.Server;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;
import org.HO.Poll;
import org.HO.SharedData;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

public class PollHandler implements Runnable{

    Poll poll;
    private Player player;
    private static final LoggingManager logger = new LoggingManager(PollHandler.class.getName());
    private SharedData sharedData = SharedData.getInstance();
    public PollHandler(Poll poll, Player player) {
        this.poll = poll;
        this.player = player;
    }

    @Override
    public void run() {

            player.writeTxt("Who do you want to be killed?");
            player.writeTxt(poll.pollChoices());
            logger.log("write poll to " + player.getName(), LogLevels.INFO);
            String vote = readWithExit(player);
            if(vote.equals("exit")) {
                removePlayer(player);
                //Thread.currentThread().interrupt();
            }else {
                poll.vote(vote, player);
                logger.log(player.getName() + " vote to " + vote, LogLevels.INFO);
            }
        Thread.currentThread().interrupt();
    }

    private void removePlayer(Player killed) {
        sharedData.killedPlayers.add(killed);
        killed.setAlive(false);
        player.writeTxt("BYE");
        killed.writeTxt("Do you want to see rest of the game?(y/n)");
        String result = killed.readTxt();
        if (result.equals("n")) {
            killed.setAbleToReadChat(false);
            sharedData.players.remove(killed);
            try {
                killed.getConnection().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String readWithExit(Player player) {
        String input = "";
        try {
            input = player.getIn().readUTF();
            if (input.equals("exit"))
                removePlayer(player);
        } catch (SocketException e) {
            player.close();
            sharedData.players.remove(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }
}
