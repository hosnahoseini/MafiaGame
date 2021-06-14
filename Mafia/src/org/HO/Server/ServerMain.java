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
//        System.out.println("1)Start new game\n2)Load game");
//        int choice = scanner.nextInt();
//        if(choice == 1) {
            int numberOfPlayer;
            while (true) {
                System.out.println("Enter number of players: ");
                numberOfPlayer = scanner.nextInt();
                if (numberOfPlayer < 3)
                    System.out.println("minimum players should be 5");
                else break;
            }
            //System.out.println("Enter port: ");
            //int port = scanner.nextInt();
            Server server = new Server(numberOfPlayer);
            server.start(7652);

//        }else {
//            System.out.println("Enter file name:");
//            Server server = new Server(fileUtils.singleObjectFileReader(scanner.nextLine()));
//            //System.out.println("Enter port: ");
//            //int port = scanner.nextInt();
//            server.start(7652);
//        }
    }

}
