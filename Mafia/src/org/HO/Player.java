package org.HO;

import java.net.Socket;

public class Player {
    private Socket connection;
    private String name;
    private PlayerRole role;
    private boolean alive = true;

    public Player(Socket connection, String name, PlayerRole role) {
        this.connection = connection;
        this.name = name;
        this.role = role;
    }

    public Socket getConnection() {
        return connection;
    }

    public String getName() {
        return name;
    }

    public PlayerRole getRole() {
        return role;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
