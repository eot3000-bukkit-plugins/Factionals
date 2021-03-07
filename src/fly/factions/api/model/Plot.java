package fly.factions.api.model;

public interface Plot extends Savable {
    void setFaction(Faction faction);

    int getLocationId();

    Faction getFaction();
}
