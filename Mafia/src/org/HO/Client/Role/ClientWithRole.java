package org.HO.Client.Role;

import org.HO.Player;

public abstract class ClientWithRole {

    private Player player;

    public ClientWithRole(Player player) {
        this.player = player;
    }

    public abstract void start();

}
