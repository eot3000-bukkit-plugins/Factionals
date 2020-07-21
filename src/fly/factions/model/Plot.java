package fly.factions.model;

import fly.factions.Factionals;
import fly.factions.permissions.GroupPermission;
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
    private PlotType type;
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

        Plot plot = new Plot(faction, owner, location);

        for(ConfigurationSection perms : (List<ConfigurationSection>) section.getList("permissions")) {
            PlotOwner permOwner = PlotOwner.getPlotOwner(perms.getInt("ownerType"), perms.getString("owner"));

            PlotPermissionList permissions = plot.getOrCreatePermission(permOwner);

            for(String s : perms.getStringList("permissions")) {
                permissions.addPermission(PlotPermission.valueOf(s));
            }
        }

        faction.claim(plot);
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

            this.forSale = false;
            this.cost = 0;

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
        this.cost = Math.abs(cost);
    }

    public boolean sell(PlotOwner owner) {
        return setOwner(owner);
    }

    public PlotType getType() {
        return type;
    }

    public void setType(PlotType type) {
        this.type = type;
    }

    @Override
    public Map<String, Object> saveInfo() {
        Map<String, Object> ret = new HashMap<>();
        List<Map<String, Object>> permissionsList = new ArrayList<>();

        for(Map.Entry<PlotOwner, PlotPermissionList> entry : permissions.entrySet()) {
            Map<String, Object> insert = new HashMap<>();
            List<String> permissions = new ArrayList<>();

            insert.put("ownerType", entry.getKey().id());
            insert.put("owner", entry.getKey().uniqueId());

            for(PlotPermission perm : entry.getValue().getPermissions()) {
                permissions.add(perm.toString());
            }
            insert.put("permissions", permissions);

            permissionsList.add(insert);
        }

        ret.put("world", location.world.getName());
        ret.put("x", location.x);
        ret.put("z", location.z);
        ret.put("faction", faction.name);
        ret.put("ownerType", owner.id());
        ret.put("owner", owner.uniqueId());
        ret.put("permissions", permissionsList);

        if(forSale) {
            ret.put("cost", cost);
        } else {
            ret.put("cost", -1);
        }

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

    public enum PlotType {
        NORMAL,
        HOTEL,
        SHOP,
        ROAD,
        MILITARY,
        VILLAGE_HOUSING,
        VILLAGE_WORKING,
        VILLAGE_SCHOOL,
    }
}
