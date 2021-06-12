package org.HO.Server;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;
import org.HO.Poll;
import org.HO.SharedData;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
/**
 * A class for handling poll
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
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
            try {

                player.writeTxt("Who do you want to be killed?");
                player.getOutObj().writeObject(poll.getPoll().keySet());
                logger.log("write poll to " + player.getName(), LogLevels.INFO);
                String vote = readWithExit(player);

                if (vote.equals("exit")) {
                    removePlayer(player);

                } else {
                    poll.vote(vote, player);
                    System.out.println("current poll res\n" + poll.getPollResult());

                }
            }catch (SocketException e) {
                player.close();
                sharedData.players.remove(player);
            }catch (IOException e) {
                e.printStackTrace();
            }
        Thread.currentThread().interrupt();
    }

    /**
     * remove player
     * @param killed player to be removed
     */
    private void removePlayer(Player killed) {
        sharedData.killedPlayers.add(killed);
        killed.setAlive(false);
        killed.writeTxt("Do you want to see rest of the game?(y/n)");
        String result = killed.readTxt();
        if (result.equals("n")) {
            sharedData.players.remove(killed);
            player.close();
            try {
                killed.getConnection().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * read message from client and check if he wants to exit or disconnected
     * @param player client
     * @return message
     */
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
