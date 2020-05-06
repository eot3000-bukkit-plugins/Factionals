package fly.factions.model;

import fly.factions.Factionals;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plot implements Savable {
    private Faction faction;
    private PlotOwner owner;
    private PlotLocation location;
    private Map<PlotOwner, PlotPermissionList> permissions = new HashMap<>();

    private boolean forSale;
    private int cost;

    public Plot(Faction faction, PlotOwner owner, PlotLocation location) {
        this.faction = faction;
        this.owner = owner;
        this.location = location;

        owner.addPlot(this);
        Factionals.getFactionals().addPlot(this);
    }

    public static void createNew(YamlConfiguration configuration) {
        ConfigurationSection section = configuration.getConfigurationSection("plot");

        PlotOwner owner = PlotOwner.getPlotOwner(section.getInt("ownerType"), section.getString("owner"));
        PlotLocation location = new PlotLocation(section.getInt("x"), section.getInt("z"), Bukkit.getWorld(section.getString("world")));
        Faction faction = (Faction) Factionals.getFactionals().getGroupByName(section.getString("faction"));

        faction.claim(new Plot(faction, owner, location));
    }

    public Faction getFaction() {
        return faction;
    }

    public PlotOwner getOwner() {
        return owner;
    }

    public PlotLocation getLocation() {
        return location;
    }

    public boolean setOwner(PlotOwner owner) {
        if(owner.getMoney() >= cost) {
            owner.removeMoney(cost);

            this.owner.addMoney(cost);
            this.owner.removePlot(this);
            this.owner = owner;
            this.owner.addPlot(this);

            return true;
        } else {
            return false;
        }
    }

    public List<PlotOwner> getPermissibleObjects() {
        return new ArrayList<>(permissions.keySet());
    }

    public PlotPermissionList getPermissionsFor(PlotOwner owner) {
        return permissions.get(owner);
    }

    public PlotPermissionList getOrCreatePermission(PlotOwner owner) {
        if(permissions.get(owner) == null) {
            permissions.put(owner, new PlotPermissionList());
        }
        return getPermissionsFor(owner);
    }

    public boolean isForSale() {
        return forSale;
    }

    public int getCost() {
        return cost;
    }

    public void setForSale(boolean forSale) {
        this.forSale = forSale;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean sell(PlotOwner owner) {
        owner.addMoney(cost);
        return setOwner(owner);
    }

    @Override
    public Map<String, Object> saveInfo() {
        Map<String, Object> ret = new HashMap<>();

        ret.put("world", location.world.getName());
        ret.put("x", location.x);
        ret.put("z", location.z);
        ret.put("faction", faction.name);
        ret.put("ownerType", owner.id());
        ret.put("owner", owner.uniqueId());
        //TODO: permissions
        ret.put("permissions", new ArrayList<>());

        return ret;
    }

    public static class PlotPermissionList {
        private List<PlotPermission> permissions = new ArrayList<>();

        public PlotPermissionList addPermission(PlotPermission perm) {
            permissions.add(perm);
            return this;
        }

        public PlotPermissionList removePermission(PlotPermission perm) {
            permissions.remove(perm);
            return this;
        }

        public List<PlotPermission> getPermissions() {
            return new ArrayList<>(permissions);
        }
    }

    public enum PlotPermission {
        SWITCH,
        DOOR,
        TRAPDOOR,
        VEHICLE,
        BUILD,
        CONTAINER,
        OTHER_INTERACT
    }
}
