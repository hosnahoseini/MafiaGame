package org.HO.Server;

import java.util.Scanner;

public class ServerMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of players: ");
        int numberOfPlayer = scanner.nextInt();
        //System.out.println("Enter port: ");
        //int port = scanner.nextInt();
        Server server = new Server(numberOfPlayer);
        server.start(8080);
    }
}
