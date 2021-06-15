package org.HO.Client;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;

import java.io.BufferedReader;
import java.io.IOException;
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
    private static final long CHAT_TIME = 5 * 60000;
    private Player player;
    private static final LoggingManager logger = new LoggingManager(WriteThread.class.getName());
    private boolean running = true;
    private String str = "";
    private Timer timer = new Timer();
    private BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
    private ClientInputHandling clientInputHandling = new ClientInputHandling();

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            running = false;
            player.writeTxtClient("Chat time ended");
            timer.cancel();
        }
    };

    public WriteThread(Player player) {
        this.player = player;

    }

    @Override
    public void run() {
        try {
            Timer timer = new Timer();
            timer.schedule(task, CHAT_TIME);
            do {
                while (!scanner.ready()) {
                    Thread.sleep(5);
                    if (!running){
                        return;
                    }
                }
                str = scanner.readLine();
                if(str.equals("exit")) {
                    player.writeTxt("exit");
                    timer.cancel();
                    return;
                }
                player.writeTxtClient(str);
                logger.log("writer write " + str , LogLevels.INFO);
            } while (!str.equalsIgnoreCase("done") && !str.equals("exit") && running);
            timer.cancel();

        } catch (IOException e) {
            System.err.println("Some went wrong in I/O player" + player);
        } catch (InterruptedException e) {
            System.err.println("interrupting while sleeping");
        }
    }

}
