package fly.factions.menus;

import fly.factions.Factionals;
import fly.factions.model.Faction;
import fly.factions.model.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class YourselfButton extends Menu.CustomButton {
    @Override
    public ItemStack getItemStack(Player player) {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        User user = Factionals.getFactionals().getUserFromPlayer(player);
        Faction faction = user.getFaction();

        meta.setOwningPlayer(player);
        meta.setDisplayName(ChatColor.GREEN + player.getName());
        meta.setLore(Arrays.asList(ChatColor.GOLD + "Balance: " + user.getMoney(), ChatColor.YELLOW + "Faction: " + (faction == null ? "" : faction.getName())));

        meta.getPersistentDataContainer().set(Menu.MENU_NAMESPACE, PersistentDataType.STRING, "members");

        stack.setItemMeta(meta);

        return stack;
    }
}
