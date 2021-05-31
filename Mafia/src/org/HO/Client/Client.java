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
    private Scanner scanner = new Scanner(System.in);
    private DataInputStream in;
    private DataOutputStream out;
    private ObjectInputStream inObj;
    private ObjectOutputStream outObj;

    public void startClient(String ipAddress, int port) {
        try (Socket connection = new Socket(ipAddress, port)) {
            in = new DataInputStream(connection.getInputStream());
            out = new DataOutputStream(connection.getOutputStream());
            inObj = new ObjectInputStream(in);
            outObj = new ObjectOutputStream(out);

            System.out.println("connected to server");
            setName();
            role = (PlayerRole) inObj.readObject();
            System.out.println("Welcome to our game \nyour role is -> " + role);
            System.out.println("type GO whenever you are ready to play");
            scanner.next();
            outObj.writeObject(true);

            while (true) {
                String input = in.readUTF();
                System.out.println(input);
                if (input.equals("MORNING"))
                    break;
            }

            System.out.println("hi");
            //clientType(connection);


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void setName() throws IOException, ClassNotFoundException {
        System.out.println("Enter your name: ");
        while (true) {
            name = scanner.next();
            outObj.writeObject(name);
            if ((boolean) inObj.readObject())
                break;
            System.out.println("Some one use this name before:( please try another one");
        }
    }

}


