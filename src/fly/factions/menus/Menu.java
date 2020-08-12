package fly.factions.menus;

import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class Menu implements Listener {
    public static Map<String, Menu> MENUS = new HashMap<>();

    private Map<Integer, Pair<Button, ItemStack>> items = new HashMap<>();
    private String name;

    protected Menu(String name) {
        this.name = name;

        MENUS.put(name, this);
    }

    protected void set(int index, Pair<Button, ItemStack> item) {
        items.put(index, item);
    }

    protected ItemStack withName(ItemStack stack, String name) {
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        stack.setItemMeta(meta);

        return stack;
    }

    public static void open(Menu menu, Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, menu.name);

        for(int x : menu.items.keySet()) {
            inventory.setItem(x, menu.items.get(x).getValue());
        }

        player.openInventory(inventory);
    }

    public static void onClick(InventoryClickEvent event) {
        Menu menu = Menu.MENUS.get(event.getView().getTitle());

        if(menu != null) {
            menu.items.get(event.getSlot()).getKey().onClick(event.getClick(), (Player) event.getWhoClicked());
        }
    }

    public interface Button {
        void onClick(ClickType type, Player player);
    }
}
