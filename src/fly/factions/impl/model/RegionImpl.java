package fly.factions.impl.model;

import fly.factions.api.model.Faction;
import fly.factions.api.model.Plot;
import fly.factions.api.model.Region;
import fly.factions.api.model.User;
import fly.factions.api.permissions.FactionPermission;
import fly.factions.api.permissions.Permissibles;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RegionImpl implements Region {
    private Faction faction;

    private String name;

    private List<Plot> plots = new ArrayList<>();

    private User governor;
    private Set<User> members = new HashSet<>();

    private Color fillColor = Color.fromRGB(255,255,255);
    private double fillOpacity = 0.3;

    private Color borderColor = Color.fromRGB(255,255,255);

    public RegionImpl(String name, User governor, Faction faction) {
        this.name = name;
        this.governor = governor;
        this.faction = faction;

        Permissibles.add(faction.getName() + ":" + name, this);
        Permissibles.add(getId(), this);
    }

    @Override
    public Collection<Plot> getPlots() {
        return new ArrayList<>(plots);
    }

    @Override
    public void addPlot(Plot plot) {
        plots.add(plot);
    }

    @Override
    public void removePlot(Plot plot) {
        plots.remove(plot);
    }

    @Override
    public String getDesc() {
        return "<div class=\"regioninfo\"><div class=\"infowindow\"><span style=\"font-size:120%;\">" + name + "</span><br />" +
                "<span style=\"font-weight:bold;\">Faction: " + faction.getName() + "</span><br />" +
                "<span style=\"font-weight:bold;\">Leader: " + getLeader().getName() + "</span></div></div>";
    }

    @Override
    public Color getFillColor() {
        return fillColor;
    }

    @Override
    public void setFillColor(Color color) {
        this.fillColor = color;
    }

    @Override
    public double getFillOpacity() {
        return fillOpacity;
    }

    @Override
    public void setFillOpacity(double d) {
        this.fillOpacity = d;
    }

    @Override
    public Color getBorderColor() {
        return borderColor;
    }

    @Override
    public void setBorderColor(Color color) {
        this.borderColor = color;
    }

    @Override
    public double getBorderOpacity() {
        return 1;
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
    public double getBalance() {
        return 0;
    }

    @Override
    public void setBalance(double x) {

    }

    @Override
    public void addToBalance(double x) {

    }

    @Override
    public void takeFromBalance(double x) {

    }

    @Override
    public String getId() {
        return faction.getId() + "-region-" + name;
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

    @Override
    public boolean userHasPlotPermissions(User user, boolean owner, boolean pub) {
        return owner ? governor.equals(user) : members.contains(user);
    }
}
