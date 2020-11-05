package fly.factions.model;

import fly.factions.Factionals;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class User implements EconomyMember {
    public static final BiConsumer<String, Player> defaultFlag = (x, y) -> {};

    private String name;
    private UUID uuid;
    private Faction faction;
    private boolean claimMode;

    private BiConsumer<String, Player> chatFlag = defaultFlag;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.name = Bukkit.getOfflinePlayer(uuid).getName();
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public void flagChat(BiConsumer<String, Player> consumer) {
        chatFlag = consumer;
    }

    public void onChat(AsyncPlayerChatEvent event) {
        if(event.getPlayer().getUniqueId().equals(uuid) && !chatFlag.equals(defaultFlag)) {
            chatFlag.accept(event.getMessage(), event.getPlayer());
            chatFlag = defaultFlag;

            event.setCancelled(true);
        }
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
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

    public boolean isClaimMode() {
        return claimMode;
    }

    public void setClaimMode(boolean claimMode) {
        this.claimMode = claimMode;
    }
}
