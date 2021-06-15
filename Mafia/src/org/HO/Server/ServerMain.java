package org.HO.Server;

import org.HO.FileUtils;

import java.util.Scanner;

/**
 * A class for run server
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class ServerMain {

    private static FileUtils fileUtils = new FileUtils();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

            int numberOfPlayer;
            while (true) {
                System.out.println("Enter number of players: ");
                numberOfPlayer = scanner.nextInt();
                if (numberOfPlayer < 3)
                    System.out.println("minimum players should be 5");
                else break;
            }
            System.out.println("Enter port: ");
            int port = scanner.nextInt();
            Server server = new Server(numberOfPlayer);
            server.start(port);

    }

}
