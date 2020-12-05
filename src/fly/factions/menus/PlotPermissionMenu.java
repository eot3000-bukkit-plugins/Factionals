package fly.factions.menus;

import fly.factions.Factionals;
import fly.factions.model.Faction;
import fly.factions.model.Plot;
import fly.factions.model.User;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.UUID;

public class PlotPermissionMenu extends Menu {
    @Override
    public void runButtonClick(InventoryClickEvent event) {
        PersistentDataContainer container = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
        int plotLocation = container.get(NUMBER_NAMESPACE, PersistentDataType.INTEGER);

        if(event.getCurrentItem().getType().equals(Material.LIME_DYE)) {
            User user = Factionals.getFactionals().getUserFromPlayer((Player) event.getWhoClicked());
            Faction owner = Factionals.getFactionals().getPlotOwner(plotLocation);

            event.getWhoClicked().sendMessage(ChatColor.GREEN + "Write the name of who you want to add permissibly in chat, with the type before it (can be player or faction)");
            user.flagChat((x, y) -> {
                if (x.startsWith("faction ")) {

                }
                if (x.startsWith("player ")) {
                    User add = Factionals.getFactionals().getUserFromName(x.replaceFirst("player ", ""));
                    Pair<String, int[]> plot = Factionals.getFactionals().getPlotOwner(plotLocation).getPlots().get(plotLocation);

                    String newKey = plot.getKey() + "u" + add.getUuid() + ";";

                    Pair<String, int[]> newPlot = new Pair<>(newKey, plot.getValue());

                    owner.setPlot(plotLocation, newPlot);
                }
            });
        }
        if(event.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
            Menu.openMenu((Player) event.getWhoClicked(), plotLocation + ";" + "u;" + container.get(UUID_NAMESPACE, PersistentDataType.STRING));
        }
    }

    @Override
    public Inventory createInventory(Player player, String info) {
        String[] options = info.split(";");

        int plotId = Plot.getLocationId(player.getLocation());

        Inventory inventory;
        Faction faction = Factionals.getFactionals().getPlotOwner(plotId);

        if(options.length == 0) {
            inventory = Bukkit.createInventory(player, 27, "&4Edit permissions");

            if (!(faction == null || faction.isDeleted())) {
                Pair<String, int[]> plot = faction.getPlots().get(plotId);
                String[] members = plot.getKey().split(";");

                for (String member : members) {
                    if (member.startsWith("u")) {
                        member = member.replaceFirst("u", "");

                        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta meta = (SkullMeta) stack.getItemMeta();

                        meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(member)));
                        meta.setDisplayName(ChatColor.RED + Bukkit.getOfflinePlayer(UUID.fromString(member)).getName());
                        meta.getPersistentDataContainer().set(MENU_NAMESPACE, PersistentDataType.STRING, "plot-perms");
                        meta.getPersistentDataContainer().set(UUID_NAMESPACE, PersistentDataType.STRING, member);
                        meta.getPersistentDataContainer().set(NUMBER_NAMESPACE, PersistentDataType.INTEGER, plotId);

                        stack.setItemMeta(meta);

                        inventory.addItem(stack);
                    }
                    if (member.startsWith("f")) {
                        member = member.replaceFirst("f", "");

                        ItemStack stack = new ItemStack(Material.WHITE_BANNER);
                        ItemMeta meta = stack.getItemMeta();

                        meta.setDisplayName(ChatColor.RED + member);
                        meta.getPersistentDataContainer().set(MENU_NAMESPACE, PersistentDataType.STRING, "plot-perms");
                        meta.getPersistentDataContainer().set(UUID_NAMESPACE, PersistentDataType.STRING, member);
                        meta.getPersistentDataContainer().set(X_NAMESPACE, PersistentDataType.INTEGER, plotId);

                        stack.setItemMeta(meta);

                        inventory.addItem(stack);
                    }
                }
            }
        } else {
            if(options.length == 4) {
                int permissionChange = Integer.parseInt(options[3]);
                //boolean currentValue = getBit()

            }

            plotId = Integer.parseInt(options[0]);
            inventory = Bukkit.createInventory(player, 9, "&4Edit permissions");

            
        }

        ItemStack addition = new ItemStack(Material.LIME_DYE);
        ItemMeta meta = addition.getItemMeta();

        meta.getPersistentDataContainer().set(MENU_NAMESPACE, PersistentDataType.STRING, "plot-perms");
        meta.getPersistentDataContainer().set(X_NAMESPACE, PersistentDataType.INTEGER, player.getChunk().getX());
        meta.getPersistentDataContainer().set(Z_NAMESPACE, PersistentDataType.INTEGER, player.getChunk().getZ());
        meta.getPersistentDataContainer().set(WORLD_NAMESPACE, PersistentDataType.STRING, player.getChunk().getWorld().getUID().toString());
        meta.setDisplayName(ChatColor.GREEN + "Add another object");

        addition.setItemMeta(meta);

        inventory.addItem(addition);

        return inventory;
    }

    private int getBit(int n, int k) {
        return (n >> k) & 1;
    }
}
