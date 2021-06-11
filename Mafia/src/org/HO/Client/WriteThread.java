package org.HO.Client;

import org.HO.Logger.LoggingManager;
import org.HO.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
/**
 * A class to handle a thread to writing chat in client side
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class WriteThread extends Thread {
    private Player player;
    private static final LoggingManager logger = new LoggingManager(WriteThread.class.getName());
    private boolean running = true;
    private String str = "";
    private Timer timer = new Timer();
    private BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            running = false;
//            timer.cancel();
        }
    };

    public WriteThread(Player player) {
        this.player = player;

    }

    @Override
    public void run() {
        try {
            Timer timer = new Timer();
            timer.schedule(task, 10 * 1000);
            do {
                while (!scanner.ready()) {
                    Thread.sleep(50);
                    if (!running)
                        return;
                }
                str = scanner.readLine();
                player.writeTxt(str);

            } while (!str.equalsIgnoreCase("done") && !str.equals("exit") && running);
            System.out.println("END WRITE");
            timer.cancel();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
