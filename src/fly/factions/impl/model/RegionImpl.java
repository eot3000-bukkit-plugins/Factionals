package fly.factions.impl.model;

import fly.factions.api.model.Faction;
import fly.factions.api.model.Lot;
import fly.factions.api.model.Region;
import fly.factions.api.model.User;
import fly.factions.api.permissions.Permissibles;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RegionImpl extends AbstractLandAdministrator implements Region {
    private Map<Integer, Lot> lots = new HashMap<>();

    private Faction faction;

    public RegionImpl(String name, User leader, Faction faction) {
        super(name, leader);

        this.faction = faction;

        Permissibles.add(faction.getName() + ":" + name, this);
        Permissibles.add(getId(), this);
    }

    @Override
    public String getDesc() {
        return "<div class=\"regioninfo\"><div class=\"infowindow\"><span style=\"font-size:120%;\">" + name + "</span><br />" +
                "<span style=\"font-weight:bold;\">Faction: " + faction.getName() + "</span><br />" +
                "<span style=\"font-weight:bold;\">Leader: " + getLeader().getName() + "</span></div></div>";
    }

    @Override
    public double getBorderOpacity() {
        return 1;
    }

    @Override
    public String getId() {
        return faction.getId() + "-region-" + name;
    }

    @Override
    public ItemStack getItem() {
        //TODO: Fix
        return new ItemStack(Material.AIR);
    }

    @Override
    public boolean userHasPlotPermissions(User user, boolean owner, boolean pub) {
        return owner ? leader.equals(user) : members.contains(user);
    }

    @Override
    public void removeMember(User user) {
        members.remove(user);

        if(user.equals(leader)) {
            this.leader = faction.getLeader();
        }
    }

    @Override
    public Map<Integer, Lot> getLots() {
        return new HashMap<>(lots);
    }

    @Override
    public void setLot(int lotNumber, Lot lot) {
        lots.put(lotNumber, lot);
    }
}
