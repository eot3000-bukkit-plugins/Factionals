/*package fly.factions.impl.listeners;

import fly.factions.api.model.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenusListener extends ListenerImpl {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null) {
            return;
        }

        User user = getUserFromPlayer((Player) event.getWhoClicked());

        if(user.getMenu() != null) {
            user.getMenu().getButton(event.getSlot()).onClick(user, event.getClick());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        getUserFromPlayer((Player) event.getPlayer()).setMenu(null);
    }
}*/
