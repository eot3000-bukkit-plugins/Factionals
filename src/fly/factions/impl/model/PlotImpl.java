package fly.factions.impl.model;

import fly.factions.Factionals;
import fly.factions.api.model.*;
import fly.factions.api.permissions.PlotPermission;
import fly.factions.impl.util.Plots;
import javafx.util.Pair;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class PlotImpl implements Plot {
    private final int x;
    private final int z;
    private final World w;

    private Faction faction;
    private LandAdministrator admin;

    private Map<Pair<Integer, Integer>, Integer> areas = new HashMap<>();

    public PlotImpl(int x, int z, World w, Faction faction) {
        this.x = x;
        this.z = z;
        this.w = w;

        setFaction(faction);
    }

    @Override
    public void setFaction(Faction faction) {
        Factionals.getFactionals().getRegistry(Plot.class, Integer.class).set(getLocationId(), this);

        if(this.faction != null) {
            this.faction.removePlot(this);
        }

        this.faction = faction;

        this.faction.addPlot(this);

        this.setAdministrator(faction);
    }

    @Override
    public int getLocationId() {
        return Plots.getLocationId(x, z, w);
    }

    @Override
    public Faction getFaction() {
        return faction;
    }

    @Override
    public LandAdministrator getAdministrator() {
        return admin;
    }

    @Override
    public void setAdministrator(LandAdministrator administrator) {
        if(admin != null && !(admin instanceof Faction)) {
            admin.removePlot(this);
        }

        this.admin = administrator;

        admin.addPlot(this);
    }

    @Override
    public Lot getLot(Location location) {
        Chunk c = location.getChunk();

        if(admin instanceof Region) {
            if (c.getZ() == z && c.getX() == x && c.getWorld().equals(w)) {
                return ((Region) admin).getLots().get(areas.get(new Pair<>(location.getBlockX(), location.getBlockZ())));
            }
        }

        return null;
    }

    @Override
    public void setLot(Location location, Lot lot) {
        Chunk c = location.getChunk();

        if(admin instanceof Region) {
            if (c.getZ() == z && c.getX() == x && c.getWorld().equals(w) && lot.getWorld().equals(w)) {
                areas.put(new Pair<>(location.getBlockX(), location.getBlockZ()), lot.getId());
            }
        }
    }

    @Override
    public Map<Pair<Integer, Integer>, Integer> getLocations() {
        return new HashMap<>(areas);
    }
}
