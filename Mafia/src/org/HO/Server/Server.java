package org.HO.Server;

import org.HO.SharedData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public void start(int port) {

        ExecutorService pool = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(port)){
            for (int i = 0; i < SharedData.numberOfPlayers; i++) {
                Socket connection = serverSocket.accept();
                pool.execute(new ClientHandler(connection));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
