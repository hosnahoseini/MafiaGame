package org.HO.Server;

import org.HO.Logger.LogLevels;
import org.HO.Player;
import org.HO.SharedData;

import java.io.IOException;
import java.net.SocketException;

public class ServerOutputHandling {
    private SharedData sharedData = SharedData.getInstance();

    /**
     * read message from client and check if he wants to exit or disconnected
     *
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

    /**
     * remove a player from game
     *
     * @param killed player to be removed
     */
    public void removePlayer(Player killed) {

        if (killed.isAlive()) {
            sharedData.killedPlayers.add(killed);
            killed.setAlive(false);

            try {
                killed.writeTxt("You've been killed:(");
                killed.writeTxt("Do you want to see rest of the game?(y/n)");
            } catch (SocketException e) {
                killed.close();
                sharedData.players.remove(killed);
            }
            String result = killed.readTxt();
            if (result.equals("n")) {
                sharedData.players.remove(killed);
                killed.close();
                try {
                    killed.getConnection().close();
                } catch (IOException e) {
                    System.err.println("Some thing went wrong with Server in I/O while closing connection of player " + killed);
                }
            }
        }
    }
}
