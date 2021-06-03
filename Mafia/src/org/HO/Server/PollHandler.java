package org.HO.Server;

import org.HO.Poll;

public class PollHandler implements Runnable{

    Poll poll;

    public PollHandler(Poll poll) {
        this.poll = poll;
    }

    @Override
    public void run() {

    }
}
