package fly.factions.model;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends PlotOwner {
    private UUID uuid;
    private List<PlayerGroup> memberOf = new ArrayList<>();

    public User(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void joinOrganization(PlayerGroup group) {
        memberOf.add(group);
    }

    public void leaveOrganization(PlayerGroup group) {
        memberOf.remove(group);
    }

    @Override
    public boolean isOwner(User user) {
        return this.equals(user);
    }

    @Override
    public boolean canDo(User user) {
        return isOwner(user);
    }

    @Override
    public int id() {
        return 0;
    }

    @Override
    public String uniqueId() {
        return getUuid().toString();
    }

    @Override
    public String niceName() {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    @Override
    public void addMoney(double d) {
        economy.depositPlayer(Bukkit.getOfflinePlayer(uuid), d);
    }

    @Override
    public void removeMoney(double d) {
        economy.withdrawPlayer(Bukkit.getOfflinePlayer(uuid), d);
    }

    @Override
    public void setMoney(double d) {
        removeMoney(getMoney());
        addMoney(d);
    }

    @Override
    public double getMoney() {
        return economy.getBalance(Bukkit.getOfflinePlayer(uuid));
    }
}
