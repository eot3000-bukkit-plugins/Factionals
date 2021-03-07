package fly.factions.impl.listeners;

import fly.factions.Factionals;
import fly.factions.api.model.User;
import fly.factions.api.registries.Registry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ListenerImpl implements Listener {
    public ListenerImpl() {
        Bukkit.getPluginManager().registerEvents(this, Factionals.getFactionals());
    }

    protected User getUserFromPlayer(Player player) {
        return ((Registry<User, UUID>) Factionals.getFactionals().getRegistry(User.class)).get(player.getUniqueId());
    }

    protected void addUser(User user) {
        ((Registry<User, UUID>) Factionals.getFactionals().getRegistry(User.class)).set(user.getUniqueId(), user);
    }
}
