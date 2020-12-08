package fly.factions.menus;

import fly.factions.Factionals;
import fly.factions.model.Faction;
import fly.factions.model.User;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class BannerMenu extends Menu {
    @Override
    public void runButtonClick(InventoryClickEvent event) {
        User user = Factionals.getFactionals().getUserFromPlayer((Player) event.getWhoClicked());
        Faction faction = user.getFaction();

        faction.setBanner(event.getCurrentItem());
    }

    @Override
    public Inventory createInventory(Player player, String info) {
        Inventory inventory = Bukkit.createInventory(player, 54, "&4Choose a banner for your faction");
        Inventory playerInv = player.getInventory();

        for(int x = 0; x < 36; x++) {
            ItemStack stack = playerInv.getItem(x);

            if(stack != null && stack.getItemMeta() instanceof BannerMeta) {
                ItemStack copy = new ItemStack(stack);
                BannerMeta meta = (BannerMeta) copy.getItemMeta();

                meta.getPersistentDataContainer().set(Menu.MENU_NAMESPACE, PersistentDataType.STRING, "banners");
                //meta.addItemFlags(ItemFlag);

                copy.setItemMeta(meta);
                copy.setAmount(1);

                inventory.addItem(copy);
            }
        }

        return inventory;
    }
}
