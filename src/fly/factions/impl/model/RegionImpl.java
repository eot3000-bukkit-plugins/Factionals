package fly.factions.impl.model;

import fly.factions.api.model.Faction;
import fly.factions.api.model.Plot;
import fly.factions.api.model.Region;
import fly.factions.api.model.User;
import fly.factions.api.permissions.FactionPermission;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RegionImpl implements Region {
    private Faction faction;

    private String name;

    private List<Plot> plots = new ArrayList<>();

    private User governor;
    private Set<User> members = new HashSet<>();

    public RegionImpl(String name, User governor, Faction faction) {
        this.name = name;
        this.governor = governor;
        this.faction = faction;
    }

    @Override
    public Collection<Plot> getPlots() {
        return new ArrayList<>(plots);
    }

    @Override
    public void addPlot(Plot plot) {

    }

    @Override
    public void removePlot(Plot plot) {

    }

    @Override
    public User getLeader() {
        return governor;
    }

    @Override
    public void setLeader(User user) {
        this.governor = user;
    }

    @Override
    public boolean hasPermission(User user, FactionPermission permission) {
        return false;
    }

    @Override
    public void addMember(User user) {
        members.add(user);
    }

    @Override
    public void removeMember(User user) {
        members.remove(user);

        if(user.equals(governor)) {
            governor = faction.getLeader();
        }
    }

    @Override
    public double getMoney() {
        return 0;
    }

    @Override
    public void setMoney(double x) {

    }

    @Override
    public void addMoney(double x) {

    }

    @Override
    public void takeMoney(double x) {

    }

    @Override
    public String getId() {
        return name + "_region_of_" + faction.getId();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack getItem() {
        //TODO: Fix
        return new ItemStack(Material.AIR);
    }

    @Override
    public void broadcast(String s) {
        for(User user : members) {
            user.sendMessage(s);
        }
    }

    @Override
    public Collection<User> getMembers() {
        return new ArrayList<>(members);
    }
}
