package org.HO.Server;

import org.HO.SharedData;

import java.util.Scanner;

public class ServerMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numberOfPlayer = scanner.nextInt();
        SharedData.numberOfPlayers = numberOfPlayer;
        int port = scanner.nextInt();
        Server server = new Server();
        server.start(port);
    }
}
