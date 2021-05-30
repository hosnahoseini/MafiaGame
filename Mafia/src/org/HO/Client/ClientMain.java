package org.HO.Client;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //System.out.println("Enter port: ");
        //int port = scanner.nextInt();
        Client client = new Client();
        client.startClient("127.0.01", 8080);

    }
}
