package org.HO;

import org.HO.Server.ClientHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Poll {
    private ConcurrentHashMap<ClientHandler , Integer> choices;

    public Poll(ArrayList<ClientHandler> choices) {
        for(ClientHandler choice:choices)
            this.choices.put(choice, 0);
    }

    public void showPoll(){
        int index = 1;
        for(ClientHandler choice:choices.keySet())
            System.out.println(index ++ + " ) "+ choice);
    }

    public void vote(String choice){
        for(ClientHandler player:choices.keySet())
            if(player.getName().equals(choice)){
                int previousVote = choices.get(choice) + 1;
                choices.put(player , previousVote + 1);
            }
    }

    public void showResult(){
        for(ClientHandler player:choices.keySet())
            System.out.println(player.getName() + " : " + choices.get(player));
    }

    public ClientHandler winner(){
        ArrayList<ClientHandler> winners = new ArrayList<>();
        int max = 0;
        for(ClientHandler player:choices.keySet())
            if(choices.get(player) > max){
                max = choices.get(player);
                winners.removeAll(winners);
                winners.add(player);
            }else if(choices.get(player) == max)
                winners.add(player);
            Random random = new Random();
            return winners.get(random.nextInt(winners.size()));
    }
}
