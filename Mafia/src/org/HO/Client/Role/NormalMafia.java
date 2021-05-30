package org.HO.Client.Role;

import java.io.*;
import java.net.Socket;

public class NormalMafia {
    Socket connection;
    public NormalMafia(Socket connection) {
        this.connection = connection;
    }

    public synchronized void start() {
        try (DataInputStream in = new DataInputStream(connection.getInputStream());
             DataOutputStream out = new DataOutputStream(connection.getOutputStream())){

            while (true)
                if (in.available() > 0)
                    System.out.println(in.readUTF());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
