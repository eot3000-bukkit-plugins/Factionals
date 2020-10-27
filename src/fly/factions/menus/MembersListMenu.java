package fly.factions.menus;

import fly.factions.Factionals;
import fly.factions.messages.Messages;
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
import org.bukkit.persistence.PersistentDataType;

public class MembersListMenu extends Menu {

    @Override
    public void runButtonClick(InventoryClickEvent event) {
        int slot = event.getSlot();


    }

    @Override
    public Inventory createInventory(Player player, String info) {
        Inventory inventory = Bukkit.createInventory(player, 27, ChatColor.translateAlternateColorCodes('&', "&2Members Management"));
        Faction faction = Factionals.getFactionals().getUserFromPlayer(player).getFaction();
        ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(Messages.NOT_MEMBER_FACTION);
        itemMeta.getPersistentDataContainer().set(MENU_NAMESPACE, PersistentDataType.STRING, "members");

        itemStack.setItemMeta(itemMeta);

        if(faction == null) {
            for(int x = 0; x < 26; x++) {
                inventory.setItem(x, itemStack);
            }
        } else {
            for(User user : faction.getMembers()) {
                itemStack = new ItemStack(Material.PLAYER_HEAD);
                itemMeta = itemStack.getItemMeta();

                ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(user.getUuid()));
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2" + user.getName()));
                itemMeta.getPersistentDataContainer().set(MENU_NAMESPACE, PersistentDataType.STRING, "members");
                itemMeta.getPersistentDataContainer().set(UUID_NAMESPACE, PersistentDataType.STRING, user.getUuid().toString());

                itemStack.setItemMeta(itemMeta);

                inventory.addItem(itemStack);
            }
        }

        return inventory;
    }
}
