package fly.factions.impl.model;

import fly.factions.api.model.ExecutiveDivision;
import fly.factions.api.model.Faction;
import fly.factions.api.model.Plot;
import fly.factions.api.model.User;
import fly.factions.api.permissions.FactionPermission;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExecutiveDivisionImpl implements ExecutiveDivision {
    private Faction faction;

    private String name;

    private User leader;
    private List<User> workers = new ArrayList<>();

    public ExecutiveDivisionImpl(String name, User leader, Faction faction) {
        this.name = name;
        this.leader = leader;
        this.faction = faction;
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
    public boolean hasPermission(User user, FactionPermission permission) {
        return false;
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
        return name + "_division_of_" + faction.getId();
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
}
