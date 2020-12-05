package fly.factions.menus;

import fly.factions.Factionals;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class ColorsMenu extends Menu {
    private int number;

    public ColorsMenu(int number) {
        this.number = number;
    }

    @Override
    public void runButtonClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public Inventory createInventory(Player player, String info) {
        Inventory inventory = Bukkit.createInventory(player, 54);

        for(int i = number*54; i < (number+1)*54; i++) {
            inventory.addItem(Factionals.getFactionals().colors.get(i));
        }

        return inventory;
    }
}
