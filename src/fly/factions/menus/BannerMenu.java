package fly.factions.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class BannerMenu extends Menu {
    @Override
    public void runButtonClick(InventoryClickEvent event) {

    }

    @Override
    public Inventory createInventory(Player player, String info) {
        Inventory inventory = Bukkit.createInventory(player, 27, "&4Choose a banner for your faction");
        Inventory playerInv = player.getInventory();

        for(int x = 0; x < 36; x++) {
            ItemStack stack = playerInv.getItem(x);

            if(stack != null && stack.getItemMeta() instanceof BannerMeta) {
                inventory.addItem(stack);
            }
        }

        return inventory;
    }
}
