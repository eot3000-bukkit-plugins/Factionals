package fly.factions.api.model;

import fly.factions.api.permissions.PlotPermission;
import javafx.util.Pair;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;

public interface Lot extends LandDivision {
    World getWorld();

    boolean hasPermission(User user, PlotPermission permission);

    void setPermission(Permissible permissible, PlotPermission permission, boolean allowed);

    EnumMap<PlotPermission, Set<Permissible>> getPermissions();

    PlotOwner getOwner();

    void setOwner(PlotOwner owner);

    void setPrice(int price);

    int getPrice();

    Town getTown();

    void setTown(Town town);

    int getId();
}
