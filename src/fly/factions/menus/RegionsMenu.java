package fly.factions.menus;

import fly.factions.Factionals;
import fly.factions.model.*;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class RegionsMenu extends Menu {
    @Override
    public void runButtonClick(InventoryClickEvent event) {
        if(event.getCurrentItem().getType().equals(Material.LIME_DYE)) {
            User user = Factionals.getFactionals().getUserFromPlayer((Player) event.getWhoClicked());
            Faction owner = user.getFaction();

            event.getWhoClicked().sendMessage(ChatColor.GREEN + "Write the name of the new region");
            user.flagChat((x, y) -> {
                if(!owner.getRegions().containsKey(x)) {
                    Region region = new Region(x, owner.getLeader(), owner);

                    owner.addRegion(region);
                } else {
                    event.getWhoClicked().sendMessage("Error");
                }
            });
        }
    }

    @Override
    public Inventory createInventory(Player player, String info) {
        Inventory inventory;
        Faction faction = Factionals.getFactionals().getUserFromPlayer(player).getFaction();

        if(!faction.hasPermission(Factionals.getFactionals().getUserFromPlayer(player), Permission.USE_ALL)) {
            return Bukkit.createInventory(player, 27, "&4No permission!");
        }

        inventory = Bukkit.createInventory(player, 27, "&4Edit Regions");

        for (Region region : faction.getRegions().values()) {
            ItemStack stack = new ItemStack(Material.WHITE_BANNER);
            ItemMeta meta = stack.getItemMeta();

            meta.setDisplayName(region.getName());
            meta.getPersistentDataContainer().set(Menu.MENU_NAMESPACE, PersistentDataType.STRING, "regions");

            inventory.addItem(stack);
        }

        ItemStack addition = new ItemStack(Material.LIME_DYE);
        ItemMeta meta = addition.getItemMeta();

        meta.getPersistentDataContainer().set(MENU_NAMESPACE, PersistentDataType.STRING, "regions");
        meta.setDisplayName(ChatColor.GREEN + "Add another object");

        addition.setItemMeta(meta);

        inventory.addItem(addition);

        return inventory;
    }

    private int getBit(int n, int k) {
        return (n >> k) & 1;
    }
}
