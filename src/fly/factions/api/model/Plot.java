package fly.factions.api.model;

import fly.factions.api.permissions.PlotPermission;

import java.util.EnumMap;
import java.util.Set;

public interface Plot extends Savable, LandDivision {
    void setFaction(Faction faction);

    int getLocationId();

    Faction getFaction();

    LandAdministrator getAdministrator();

    void setAdministrator(LandAdministrator administrator);

    boolean hasPermission(User user, PlotPermission permission);

    void setPermission(Permissible permissible, PlotPermission permission, boolean allowed);

    EnumMap<PlotPermission, Set<Permissible>> getPermissions();

    PlotOwner getOwner();

    void setOwner(PlotOwner owner);

    void setPrice(int price);

    int getPrice();
}
