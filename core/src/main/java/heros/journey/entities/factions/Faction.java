package heros.journey.entities.factions;

public class Faction {

    private String name;
    private boolean playerFaction;

    public Faction(String name, boolean playerFaction) {
        this.name = name;
        this.playerFaction = playerFaction;
        FactionManager.get().put(name, this);
    }

    public Faction(String name) {
        this(name, false);
    }

    public boolean isPlayerFaction() {
        return playerFaction;
    }

    @Override
    public String toString() {
        return name;
    }

    public Faction clone(){
        Faction clone = new Faction(name, playerFaction);
        return clone;
    }
}
