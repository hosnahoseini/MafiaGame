package org.HO.Client.Role;

import org.HO.Logger.LogLevels;
import org.HO.Logger.LoggingManager;
import org.HO.Player;
import org.HO.Poll;
import org.HO.Server.Server;

import java.io.IOException;
import java.util.Scanner;

public class GodFather extends NormalMafia {
    private static final LoggingManager logger = new LoggingManager(GodFather.class.getName());

    public GodFather(Player player) {
        super(player);
    }

    @Override
    public void start() {
        super.start();
        waitUntilReceivingMsg("YOUR TURN");
        System.out.println(getPlayer().readTxt());
        String poll = getPlayer().readTxt();
        System.out.println(poll);
        Scanner scanner = new Scanner(System.in);
        String result = scanner.nextLine();
        getPlayer().writeTxt(result);
        logger.log(result, LogLevels.INFO);
    }
}
