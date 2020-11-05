package fly.factions.menus;

import fly.factions.Factionals;
import fly.factions.model.Faction;
import fly.factions.model.User;
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

import java.util.Arrays;

public class FactionTopMenu extends Menu {

    @Override
    public void runButtonClick(InventoryClickEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        PersistentDataContainer cont = itemStack.getItemMeta().getPersistentDataContainer();

        if(cont.has(FACTION_NAMESPACE, PersistentDataType.STRING)) {
            Faction faction = Factionals.getFactionals().getFactionByName(cont.get(FACTION_NAMESPACE, PersistentDataType.STRING));

            ButtonAction.ButtonActionType.OPEN_MENU.biConsumer.accept((Player) event.getWhoClicked(), "faction-top_" + faction.getName());
        }

    }

    @Override
    public Inventory createInventory(Player player, String info) {
        Inventory inventory;

        if(info.split("_").length > 1) {
            Faction faction = Factionals.getFactionals().getFactionByName(info.split("_")[1]);
            inventory = Bukkit.createInventory(player, 27, ChatColor.translateAlternateColorCodes('&', "&3Faction Info: &b " + faction.getName()));

            for(User user : faction.getMembers()) {
                ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta itemMeta = itemStack.getItemMeta();

                ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(user.getUuid()));
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2" + user.getName()));
                itemMeta.getPersistentDataContainer().set(MENU_NAMESPACE, PersistentDataType.STRING, "faction-top");
                itemMeta.getPersistentDataContainer().set(UUID_NAMESPACE, PersistentDataType.STRING, user.getUuid().toString());

                itemStack.setItemMeta(itemMeta);

                inventory.addItem(itemStack);
            }

            return inventory;
        }

        inventory = Bukkit.createInventory(player, 27, ChatColor.translateAlternateColorCodes('&', "&6Faction Top"));

        for (Faction faction : Factionals.getFactionals().getFactions()) {
            ItemStack itemStack = new ItemStack(Material.WHITE_BANNER);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c" + faction.getName()));
            itemMeta.setLore(Arrays.asList(
                    ChatColor.GOLD + "Leader: " + ((User) faction.getLeader()).getName(),
                    ChatColor.YELLOW + "Balance: " + faction.getMoney(),
                    ChatColor.GOLD + "Member Count: " + faction.getMembers().size()
            ));
            itemMeta.getPersistentDataContainer().set(MENU_NAMESPACE, PersistentDataType.STRING, "faction-top");
            itemMeta.getPersistentDataContainer().set(FACTION_NAMESPACE, PersistentDataType.STRING, faction.getName());

            itemStack.setItemMeta(itemMeta);

            inventory.addItem(itemStack);
        }

        return inventory;
    }
}
