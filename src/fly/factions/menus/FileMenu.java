package fly.factions.menus;

import com.google.common.collect.Lists;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileMenu extends Menu {
    private Map<Integer, Pair<ItemStack, ButtonAction>> stacks = new HashMap<>();
    private String title;

    FileMenu(ConfigurationSection section, String menuName) {
        for(int x = 0; x < 27; x++) {
            ConfigurationSection button = section.getConfigurationSection("" + x);

            if(button == null) {
                button = section.getConfigurationSection("default");
            }

            ItemStack buttonItem = new ItemStack(Material.valueOf(button.getString("id")));
            ItemMeta meta = buttonItem.getItemMeta();
            ButtonAction action = new ButtonAction(button);
            String desc = button.getString("desc");
            String name = button.getString("name");

            if(desc == null) {
                desc = "";
            }

            if(name == null) {
                name = "";

                meta.getPersistentDataContainer().set(TYPE_NAMESPACE, PersistentDataType.STRING, button.getString("type"));
            }

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            meta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', desc)));
            meta.getPersistentDataContainer().set(MENU_NAMESPACE, PersistentDataType.STRING, name);

            buttonItem.setItemMeta(meta);

            stacks.put(x, new Pair<>(buttonItem, action));
        }

        title = ChatColor.translateAlternateColorCodes('&', section.getString("name"));
    }

    @Override
    public void runButtonClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        stacks.get(slot).getValue().execute((Player) event.getWhoClicked());
    }

    @Override
    public Inventory createInventory(Player player, String info) {
        Inventory inventory = Bukkit.createInventory(player, 27, title);

        for(Map.Entry<Integer, Pair<ItemStack, ButtonAction>> stack : stacks.entrySet()) {
            ItemStack item = stack.getValue().getKey();
            PersistentDataContainer cont = item.getItemMeta().getPersistentDataContainer();

            if(cont.has(TYPE_NAMESPACE, PersistentDataType.STRING)) {
                item = CustomButton.getButton(cont.get(TYPE_NAMESPACE, PersistentDataType.STRING)).getItemStack(player);
            }

            inventory.setItem(stack.getKey(), item);
        }

        return inventory;
    }
}
