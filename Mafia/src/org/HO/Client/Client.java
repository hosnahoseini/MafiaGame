package org.HO.Client;

import org.HO.Client.Role.NormalMafia;
import org.HO.PlayerRole;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private PlayerRole role;
    private String name;

    public void startClient(String ipAddress, int port) {
        try (Socket connection = new Socket(ipAddress, port);
             DataInputStream in = new DataInputStream(connection.getInputStream());
             DataOutputStream out = new DataOutputStream(connection.getOutputStream());
             ObjectInputStream inObj = new ObjectInputStream(in);
             ObjectOutputStream outObj = new ObjectOutputStream(out)) {

            System.out.println("connected to server");
            outObj.writeObject(setName());
            role = (PlayerRole) inObj.readObject();
            System.out.println("your role is -> " + role);

            clientType(connection);


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public String setName(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name: ");
//        name = "a";
        name = scanner.next();
        return name;
    }
    public void clientType(Socket connection) throws InterruptedException {

        switch (role){
            case NORMAL_MAFIA -> new NormalMafia(connection).start();
        }
    }


}


