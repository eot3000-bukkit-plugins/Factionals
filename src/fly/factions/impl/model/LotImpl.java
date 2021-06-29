package fly.factions.impl.model;

import fly.factions.api.model.*;
import fly.factions.api.permissions.PlotPermission;
import org.bukkit.World;

import java.util.*;

public class LotImpl implements Lot {
    private PlotOwner owner;
    private int price;
    private Faction faction;
    private Region region;
    private int id;
    private World world;

    private EnumMap<PlotPermission, Set<Permissible>> permissionMap = new EnumMap<>(PlotPermission.class);
    private boolean publicLot;

    private Town town;

    //private List<Pair<Integer, Integer>> blocks = new ArrayList<>();

    public LotImpl(Region region, int id, World world) {
        this.faction = region.getFaction();
        this.region = region;

        for(PlotPermission permission : PlotPermission.values()) {
            permissionMap.put(permission, new HashSet<>());
        }

        this.world = world;
        this.id = id;
    }

    @Override
    public World getWorld() {
        return world;
    }

    //@Override
    //public List<Pair<Integer, Integer>> getBlocks() {
    //    return new ArrayList<>(blocks);
    //}

    @Override
    public boolean hasPermission(User user, PlotPermission permission) {
        for(Permissible permissible : permissionMap.get(permission)) {
            if(permissible.userHasPlotPermissions(user, false, publicLot)) {
                return true;
            }
        }

        return owner.userHasPlotPermissions(user, false, publicLot);
    }

    @Override
    public void setPermission(Permissible permissible, PlotPermission permission, boolean allowed) {
        if(allowed) {
            permissionMap.get(permission).add(permissible);
        } else {
            permissionMap.get(permission).remove(permissible);
        }
    }

    @Override
    public EnumMap<PlotPermission, Set<Permissible>> getPermissions() {
        EnumMap<PlotPermission, Set<Permissible>> ret = new EnumMap<>(PlotPermission.class);

        for(PlotPermission permission : PlotPermission.values()) {
            ret.put(permission, new HashSet<>(permissionMap.get(permission)));
        }

        return ret;
    }

    @Override
    public PlotOwner getOwner() {
        return owner;
    }

    @Override
    public void setOwner(PlotOwner owner) {
        this.owner = owner;
    }

    @Override
    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public Town getTown() {
        return town;
    }

    @Override
    public void setTown(Town town) {
        if(town.getRegion().equals(region)) {
            if(this.town != null) {
                this.town.removePlot(this);
            }

            this.town = town;
            town.addPlot(this);
        }
    }

    @Override
    public int getId() {
        return id;
    }
}