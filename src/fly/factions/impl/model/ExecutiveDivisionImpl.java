package fly.factions.impl.model;

import fly.factions.api.model.ExecutiveDivision;
import fly.factions.api.model.Faction;
import fly.factions.api.model.Plot;
import fly.factions.api.model.User;
import fly.factions.api.permissions.FactionPermission;
import fly.factions.api.permissions.Permissibles;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class ExecutiveDivisionImpl implements ExecutiveDivision {
    private Faction faction;

    private String name;

    private User leader;
    private List<User> workers = new ArrayList<>();

    private EnumSet<FactionPermission> permissions = EnumSet.noneOf(FactionPermission.class);

    public ExecutiveDivisionImpl(String name, User leader, Faction faction) {
        this.name = name;
        this.leader = leader;
        this.faction = faction;

        Permissibles.add(faction.getName() + ":" + name, this);
        Permissibles.add(getId(), this);
    }

    @Override
    public User getLeader() {
        return leader;
    }

    @Override
    public void setLeader(User leader) {
        this.leader = leader;
    }

    @Override
    public void addMember(User user) {
        workers.add(user);
    }

    @Override
    public void removeMember(User user) {
        workers.remove(user);

        if(user.equals(leader)) {
            leader = faction.getLeader();
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
        return faction.getId() + "-department-" + name;
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
        for(User user : workers) {
            user.sendMessage(s);
        }
    }

    @Override
    public Collection<User> getMembers() {
        return new ArrayList<>(workers);
    }

    @Override
    public void addPermission(FactionPermission permission) {
        permissions.add(permission);
    }

    @Override
    public void removePermission(FactionPermission permission) {
        permissions.remove(permission);
    }

    @Override
    public boolean canDo(FactionPermission permission) {
        return permissions.contains(permission);
    }

    @Override
    public boolean userHasPlotPermissions(User user, boolean owner, boolean pub) {
        return owner ? leader.equals(user) || faction.getLeader().equals(user) : pub ? faction.getMembers().contains(user) : workers.contains(user);
    }
}
