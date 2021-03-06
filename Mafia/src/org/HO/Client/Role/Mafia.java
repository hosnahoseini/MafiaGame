package org.HO.Client.Role;


import org.HO.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A class for client with mafia role
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class Mafia extends  ClientWithRole{

    public Mafia(Player player) {
        super(player);
    }
    /**
     * all mafia start to vote
     */
    @Override
    public void start() {
        super.start();
        mafiasVote();
    }

    /**
     * vote for mafias poll
     */
    private void mafiasVote() {
        try {
            System.out.println(getPlayer().readTxt());
            Collection<Player> poll = (Collection<Player>) getPlayer().getInObj().readObject();
            for (Player player : poll)
                System.out.println("◉ " + player);

            getInputForVote(poll);

        } catch (IOException e) {
            System.err.println("Some went wrong in I/O player" + getPlayer());
        } catch (ClassNotFoundException e) {
            System.err.println("Can't convert to Collection<Player>");
        }
    }

    /**
     * get input for vote
     * @param poll poll
     */
    public void getInputForVote(Collection<Player> poll) {
        vote = "";
        running = true;
        Timer timer = new Timer();
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                running = false;
                if (validInput(poll, vote)) {
                    getPlayer().writeTxtClient(vote);
                }
                else
                    getPlayer().writeTxtClient("");
                System.out.println("poll time ended");
                timer.cancel();
            }
        };

        timer.schedule(task, 30000);
        try {
            while (running) {
                while (!scanner.ready()) {
                    Thread.sleep(50);
                    if (!running)
                        return;
                }
                vote = scanner.readLine();
                clientInputHandling.checkIfInputIsExit(getPlayer(), vote);
                if (!validInput(poll, vote)) {
                    System.out.println("Invalid input!Try again");
                    vote = "";
                }else
                    System.out.println("thanks");

            }

        } catch (IOException e) {
            System.err.println("Some went wrong in I/O player" + getPlayer());
        } catch (InterruptedException e) {
            System.err.println("interruption while sleeping");
        }
    }

}
