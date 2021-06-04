package org.HO.Client.Role;

import org.HO.Player;
import org.HO.PlayerRole;

public class ClientFactory {

    public ClientWithRole getClient(Player player) {
        switch (player.getRole()){
            case MAYOR:
                return new Mayor(player);
            case NORMAL_MAFIA:
                return ;
        }

    }
}
