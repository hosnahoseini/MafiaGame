package org.HO.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public void startClient(String ipAddress, int port) {
        try (Socket connection = new Socket(ipAddress, port);
             DataInputStream in = new DataInputStream(connection.getInputStream());
             DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
            setName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void setName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter you name: ");
        String name = scanner.nextLine();
        // TODO: check if is not repetitive

    }

}


