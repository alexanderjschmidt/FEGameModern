package heros.journey.entities.factions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FactionManager extends HashMap<String, Faction> {

    private List<Faction> playerFactions;

    private static FactionManager factionManager;

    public static FactionManager get() {
        if (factionManager == null)
            factionManager = new FactionManager();
        return factionManager;
    }

    private FactionManager() {
        playerFactions = new ArrayList<>();
    }

    @Override
    public Faction put(String name, Faction faction) {
        Faction response = super.put(name, faction);
        if(faction.isPlayerFaction()){
            playerFactions.add(faction);
        }
        return response;
    }

    public List<Faction> getPlayerFactions() {
        return playerFactions;
    }
}
