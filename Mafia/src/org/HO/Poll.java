package org.HO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Poll implements Serializable {
    private ConcurrentHashMap<Player, ArrayList<Player>> poll;

    public Poll(Collection<Player> choices) {
        poll = new ConcurrentHashMap<>();
        for(Player choice:choices)
            this.poll.put(choice, new ArrayList<>());
    }

    public String pollChoices(){
        String result = "";
        int index = 1;
        for(Player choice: poll.keySet())
            result += (index ++ + " ) "+ choice.getName() + "\n");

        return result;
    }

    public void showPoll(){
        System.out.println(pollChoices());
    }

    public void vote(String vote, Player voter){

        for(Player choice: poll.keySet())
            if(choice.getName().equals(vote)){
                ArrayList<Player> previousVote = poll.get(choice);
                previousVote.add(voter);
                poll.put(choice , previousVote);
            }
    }

    public void showResult(){
        System.out.println(this.PollResult());
    }

    public Player winner(){
        ArrayList<Player> winners = new ArrayList<>();
        int max = 0;
        for(Player player: poll.keySet())
            if(poll.get(player).size() > max){
                max = poll.get(player).size();
                winners.removeAll(winners);
                winners.add(player);
            }else if(poll.get(player).size() == max)
                winners.add(player);
            Random random = new Random();
            return winners.get(random.nextInt(winners.size()));
    }


    public String PollResult(){
        String result = "";
        for(Player player: poll.keySet()) {
            result += (player.getName() + " : [ ");
            for (Player voters : poll.get(player))
                result += (voters.getName() + " ");
            result += ("]\n");
        }
        return result;
    }
}
