package org.HO.Client.Role;

import org.HO.Logger.LogLevels;
import org.HO.Player;

import java.io.IOException;
import java.util.Scanner;

public abstract class ClientWithRole {

    private Player player;

    public ClientWithRole(Player player) {
        this.player = player;
    }

    public void start() {
        waitUntilReceivingMsg("YOUR TURN");
    }


    public Player getPlayer() {
        return player;
    }

    public void waitUntilReceivingMsg(String msg) {
        while (true) {
            String input = null;
            try {
                input = player.getIn().readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (input.equals(msg)) {
                System.out.println(msg);
                break;
            }
        }
    }

    public String writeWithExit(Player player){
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
//        if(input.equals("exit")) {
//            player.writeTxt("exit");
//            removePlayer(player);
//        }
        return input;
    }

    private void removePlayer(Player player) {
        player.readTxt();
        System.out.println(player.readTxt());
        Scanner scanner = new Scanner(System.in);
        String result = scanner.next();
        player.writeTxt(result);
        if (result.equals("n")) {
            player.close();
            System.exit(5);
        }else {
            while (true) {
                System.out.println(player.readTxt());
            }
        }
    }
}
