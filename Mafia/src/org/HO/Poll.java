package org.HO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for a poll
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class Poll implements Serializable {
    private ConcurrentHashMap<Player, ArrayList<Player>> poll;

    /**
     * constructor
     *
     * @param choices poll choices
     */
    public Poll(Collection<Player> choices) {
        poll = new ConcurrentHashMap<>();
        for (Player choice : choices)
            this.poll.put(choice, new ArrayList<>());
    }

    /**
     * put a vote in poll
     *
     * @param vote  vote
     * @param voter player who voted
     */
    public synchronized void vote(String vote, Player voter) {

        for (Player choice : poll.keySet())
            if (choice.getName().equals(vote)) {
                ArrayList<Player> previousVote = poll.get(choice);
                previousVote.add(voter);
                poll.put(choice, previousVote);
            }
    }

    /**
     * announce winner of the vote
     *
     * @return player who have the most vote or random player between whom have same most number of vote
     */
    public Player winner() {
        ArrayList<Player> winners = new ArrayList<>();
        int max = 0;
        for (Player player : poll.keySet())
            if (poll.get(player).size() > max) {
                max = poll.get(player).size();
                winners.removeAll(winners);
                winners.add(player);
            } else if (poll.get(player).size() == max)
                winners.add(player);
        Random random = new Random();
        return winners.get(random.nextInt(winners.size()));
    }

    /**
     * get pull result as string
     *
     * @return result in form choice : [players who voted for this choice separated by " "]
     */
    public String getPollResult() {
        String result = "────────┬──────────────────────────\n" +
                " player |           voters             \n" +
                "────────┼──────────────────────────\n";
        for (Player player : poll.keySet()) {
            result += (String.format("%4s    |", player.getName()));
            for (Player voters : poll.get(player))
                result += (" " + voters.getName());
            result += ("\n────────┼──────────────────────────\n");
        }
        result += "────────┴──────────────────────────\n";
        return result;
    }

    /**
     * get poll
     *
     * @return poll
     */
    public ConcurrentHashMap<Player, ArrayList<Player>> getPoll() {
        return poll;
    }
}
