package org.HO.Client.Role;

import org.HO.Player;

/**
 * A class to make a client regarding to his role(factory design pattern)
 *
 * @author Hosna Oyarhoseini
 * @version 1.0
 */
public class ClientFactory {

    /**
     * get a client regarding to his role
     * @param player player
     * @return client with role
     */
    public ClientWithRole getClient(Player player) {
        switch (player.getRole()){
            case MAYOR:
                return new Mayor(player);
            case NORMAL_MAFIA:
                return new Mafia(player);
            case GOD_FATHER:
                return new GodFather(player);
            case DR_LECTER:
                return new DrLecter(player);
            case DR_CITY:
                return new DrCity(player);
            case DETECTIVE:
                return new Detective(player);
            case PROFESSIONAL:
                return new Professional(player);
            case PSYCHOLOGIST:
                return new Psychologist(player);
            case DIE_HARD:
                return new Diehard(player);
            case NORMAL_PEOPLE:
                return new NormalPeople(player);
        }
        return null;

    }
}
