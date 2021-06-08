package org.HO.Server;

import org.HO.FileUtils;

import java.util.Scanner;

public class ServerMain {
    private static FileUtils fileUtils = new FileUtils();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1)Start new game\n2)Load game");
        int choice = scanner.nextInt();
        if(choice == 1) {
            System.out.println("Enter number of players: ");
            int numberOfPlayer = scanner.nextInt();
            //System.out.println("Enter port: ");
            //int port = scanner.nextInt();
            Server server = new Server(numberOfPlayer);
            server.start(7652);
        }else {
            System.out.println("Enter file name:");
            Server server = new Server(fileUtils.singleObjectFileReader(scanner.nextLine()));
            //System.out.println("Enter port: ");
            //int port = scanner.nextInt();
            server.start(7652);
        }
    }

}
