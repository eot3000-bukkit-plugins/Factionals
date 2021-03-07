package fly.factions.impl.listeners;

import fly.factions.Factionals;
import fly.factions.api.model.User;
import fly.factions.api.registries.Registry;
import fly.factions.impl.model.UserImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinLeaveListener extends ListenerImpl {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(getUserFromPlayer(event.getPlayer()) == null) {
            addUser(new UserImpl(event.getPlayer().getUniqueId(), event.getPlayer().getDisplayName()));
        }
    }
}
