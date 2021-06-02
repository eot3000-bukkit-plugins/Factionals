package fly.factions.impl.model;

import fly.factions.api.model.Faction;
import fly.factions.api.model.FactionComponent;
import fly.factions.api.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFactionComponent implements FactionComponent {
    protected User leader;
    protected Set<User> members = new HashSet<>();
    protected String name;

    protected double balance;

    protected AbstractFactionComponent(String name, User leader) {
        this.leader = leader;
        this.name = name;
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
        members.add(user);
    }

    @Override
    public Collection<User> getMembers() {
        return new ArrayList<>(members);
    }


    @Override
    public void broadcast(String s) {
        for(User user : members) {
            user.sendMessage(s);
        }
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void setBalance(double x) {
        this.balance = x;
    }

    @Override
    public void addToBalance(double x) {
        balance+=x;
    }

    @Override
    public void takeFromBalance(double x) {
        balance-=x;
    }


    @Override
    public String getName() {
        return name;
    }
}
