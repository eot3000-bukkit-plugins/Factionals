package fly.factions.menus;

import fly.factions.Factionals;
import fly.factions.model.Faction;
import fly.factions.model.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class ClaimOnButton extends Menu.CustomButton {
    @Override
    public ItemStack getItemStack(Player player) {
        ItemStack stack;
        User user = Factionals.getFactionals().getUserFromPlayer(player);

        if(user.isClaimMode()) {
            stack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);

            ItemMeta meta = stack.getItemMeta();

            meta.setDisplayName(ChatColor.RED + "Click to turn claim mode off!");
            meta.getPersistentDataContainer().set(Menu.MENU_NAMESPACE, PersistentDataType.STRING, "faction-menu");
            meta.getPersistentDataContainer().set(Menu.TYPE_NAMESPACE, PersistentDataType.STRING, "claim-on");

            stack.setItemMeta(meta);
        } else {
            stack = new ItemStack(Material.RED_STAINED_GLASS_PANE);

            ItemMeta meta = stack.getItemMeta();

            meta.setDisplayName(ChatColor.GREEN + "Click to turn claim mode on!");
            meta.getPersistentDataContainer().set(Menu.MENU_NAMESPACE, PersistentDataType.STRING, "faction-menu");
            meta.getPersistentDataContainer().set(Menu.TYPE_NAMESPACE, PersistentDataType.STRING, "claim-on");

            stack.setItemMeta(meta);
        }

        return stack;
    }

    @Override
    public void runButtonClick(Player player) {
        System.out.println("yes");
        User user = Factionals.getFactionals().getUserFromPlayer(player);

        user.setClaimMode(!user.isClaimMode());

        Menu.openMenu(player, "faction-menu");
    }
}
