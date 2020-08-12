package fly.factions.model;

import org.bukkit.Bukkit;

import java.util.UUID;

public class User implements EconomyMember {
    private String name;
    private UUID uuid;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.name = Bukkit.getOfflinePlayer(uuid).getName();
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
}
