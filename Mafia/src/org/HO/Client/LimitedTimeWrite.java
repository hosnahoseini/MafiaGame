package org.HO.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class LimitedTimeWrite extends Thread {
    private volatile boolean running = true;
    private BufferedReader scanner;
    private Timer timer;
    int time;

    public LimitedTimeWrite(int time) {
        timer = new Timer();
        scanner = new BufferedReader(new InputStreamReader(System.in));
        this.time = time;
    }


    @Override
    public void run() {

        timer.schedule(task, time * 1000);
        try {
            scan();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done");
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            running = false;
            timer.cancel();

        }
    };

    private void scan() throws IOException, InterruptedException {
        while (running) {
            while (!scanner.ready()) {
                Thread.sleep(50);
                if (!running)
                    return;
            }
            String input = scanner.readLine();
            System.out.println(input);
        }
    }
}
