package org.HO.Client.Role;

import org.HO.Player;

public class ClientFactory {

    public ClientWithRole getClient(Player player) {
        switch (player.getRole()){
            case MAYOR:
                return new Mayor(player);
            case NORMAL_MAFIA:
                return new NormalMafia(player);
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
