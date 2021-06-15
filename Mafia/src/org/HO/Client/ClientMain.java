package org.HO.Client;

import java.util.Scanner;

/**
 * A class to start a new client
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class ClientMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter port: ");
        int port = scanner.nextInt();
        Client client = new Client();
        client.startClient("127.0.01", port);

    }
}
