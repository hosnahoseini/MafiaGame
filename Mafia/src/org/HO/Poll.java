package org.HO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Poll implements Serializable {
    private ConcurrentHashMap<Player, BlockingQueue<Player>> poll;

    public Poll(Collection<Player> choices) {
        poll = new ConcurrentHashMap<>();
        for(Player choice:choices)
            this.poll.put(choice, new LinkedBlockingQueue<>());
    }

    public void showPoll(){
        int index = 1;
        for(Player choice: poll.keySet())
            System.out.println(index ++ + " ) "+ choice.getName());
    }

    public void vote(String choice, Player player){

        for(Player player1: poll.keySet())
            if(player1.getName().equals(choice)){
                BlockingQueue previousVote = poll.get(choice);
                previousVote.add(player);
                poll.put(player , previousVote);
            }
    }

    public void showResult(){
        for(Player player: poll.keySet()) {
            System.out.print(player.getName() + " : [");
            for (Player voters : poll.get(player))
                System.out.print(voters + " ");
            System.out.println("]");
        }
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
}
