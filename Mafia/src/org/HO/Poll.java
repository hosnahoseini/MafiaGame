package org.HO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Poll {
    private ConcurrentHashMap<Player, BlockingQueue<Player>> poll;

    public Poll(Collection<Player> choices) {
        poll = new ConcurrentHashMap<>();
        for(Player choice:choices)
            this.poll.put(choice, new LinkedBlockingQueue<>());
    }

    public void showPoll(){
        int index = 1;
        for(Player choice: poll.keySet())
            System.out.println(index ++ + " ) "+ choice);
    }

    public void vote(int index, Player player){
        Player choice = (Player) poll.keySet().toArray()[index];
        BlockingQueue previousVote = poll.get(choice);
        previousVote.add(player);
        poll.put(player , previousVote);

//        for(Player player1: poll.keySet())
//            if(player1.getName().equals(choice)){
//                BlockingQueue previousVote = poll.get(choice);
//                previousVote.add(player);
//                poll.put(player , previousVote);
//            }
    }

    public void showResult(){
        for(Player player: poll.keySet())
            System.out.println(player.getName() + " : " + poll.get(player));
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
