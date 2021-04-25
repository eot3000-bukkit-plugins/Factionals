package fly.factions.impl.model;

import fly.factions.Factionals;
import fly.factions.api.model.*;
import fly.factions.api.permissions.PlotPermission;
import fly.factions.impl.util.Plots;
import org.bukkit.World;

import java.util.*;

public class PlotImpl implements Plot {
    private final int x;
    private final int z;
    private final World w;

    private Faction faction;
    private LandAdministrator admin;
    private PlotOwner owner;
    private int price;

    private EnumMap<PlotPermission, Set<Permissible>> permissionMap = new EnumMap<>(PlotPermission.class);
    private boolean publicPlot;

    public PlotImpl(int x, int z, World w, Faction faction) {
        this.x = x;
        this.z = z;
        this.w = w;

        setFaction(faction);

        for(PlotPermission permission : PlotPermission.values()) {
            permissionMap.put(permission, new HashSet<>());
        }
    }

    @Override
    public void setFaction(Faction faction) {
        Factionals.getFactionals().getRegistry(Plot.class, Integer.class).set(getLocationId(), this);

        if(this.faction != null) {
            this.faction.removePlot(this);
        }

        this.faction = faction;

        this.faction.addPlot(this);

        this.setOwner(faction);
        this.setPrice(-1);

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
    public boolean hasPermission(User user, PlotPermission permission) {
        for(Permissible permissible : permissionMap.get(permission)) {
            if(permissible.userHasPlotPermissions(user, false, publicPlot)) {
                return true;
            }
        }

        return owner.userHasPlotPermissions(user, false, publicPlot);
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
}
