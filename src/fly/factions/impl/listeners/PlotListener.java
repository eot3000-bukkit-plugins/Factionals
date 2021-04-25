package fly.factions.impl.listeners;

import fly.factions.Factionals;
import fly.factions.api.model.Faction;
import fly.factions.api.model.Plot;
import fly.factions.api.model.User;
import fly.factions.api.permissions.PlotPermission;
import fly.factions.api.registries.Registry;
import fly.factions.impl.util.Plots;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class PlotListener extends ListenerImpl {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Registry<Plot, Integer> pr = Factionals.getFactionals().getRegistry(Plot.class, Integer.class);

        Plot to = pr.get(Plots.getLocationId(e.getTo()));
        Plot from = pr.get(Plots.getLocationId(e.getFrom()));

        if(to == null && from != null) {
            e.getPlayer().sendTitle(ChatColor.DARK_GREEN + "Entering Wilderness", ChatColor.GREEN + "It's dangerous to go alone", 5, 25, 5);
        } else {
            Faction fromFaction = from != null ? from.getFaction() : null;
            String fromFactionName = fromFaction != null ? fromFaction.getName() : ChatColor.GREEN + "Wilderness";

            if(to != null) {
                if (to.getFaction() != fromFaction) {
                    e.getPlayer().sendTitle(ChatColor.GOLD + "Entering " + to.getFaction().getName(), ChatColor.YELLOW + "Leaving " + fromFactionName, 5, 25, 5);
                }

                if(to != from) {
                    if(to.getPrice() != -1) {
                        e.getPlayer().sendMessage(ChatColor.GREEN + "[For Sale: " + to.getPrice() + "] " + ChatColor.GOLD + "Owner: " + to.getOwner().getName());
                    } else {
                        e.getPlayer().sendMessage(ChatColor.GOLD + "Owner: " + to.getOwner().getName());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Registry<Plot, Integer> pr = Factionals.getFactionals().getRegistry(Plot.class, Integer.class);

        if (event.getClickedBlock() == null) {
            return;
        }

        Plot plot = pr.get(Plots.getLocationId(event.getClickedBlock().getLocation()));

        if (plot == null) {
            return;
        }

        if (event.getItem() != null) {
            Material t = event.getItem().getType();

            if (!plot.hasPermission(getUserFromPlayer(event.getPlayer()), PlotPermission.VEHICLES)) {
                if (Tag.ITEMS_BOATS.isTagged(t)) {
                    event.setCancelled(true);
                }

                if (t.equals(Material.MINECART) || t.equals(Material.CHEST_MINECART) || t.equals(Material.COMMAND_BLOCK_MINECART) || t.equals(Material.FURNACE_MINECART) ||
                        t.equals(Material.HOPPER_MINECART) || t.equals(Material.TNT_MINECART)) {
                    event.setCancelled(true);
                }
            }

            if (!plot.hasPermission(getUserFromPlayer(event.getPlayer()), PlotPermission.DETAILS)) {
                if (t.equals(Material.PAINTING) || t.equals(Material.ITEM_FRAME) || t.equals(Material.ARMOR_STAND)) {
                    event.setCancelled(true);
                }
            }

            if (!plot.hasPermission(getUserFromPlayer(event.getPlayer()), PlotPermission.BUILD)) {
                if (t.equals(Material.FLINT_AND_STEEL)) {
                    event.setCancelled(true);
                }
            }
        }

        for (PlotPermission permission : PlotPermission.values()) {
            System.out.println(event.getPlayer().getName() + " " + permission + " " + permission.required(event.getClickedBlock(), event.getAction(), event.getPlayer().isSneaking()) + " " + plot.hasPermission(getUserFromPlayer(event.getPlayer()), permission));

            if (permission.required(event.getClickedBlock(), event.getAction(), event.getPlayer().isSneaking()) && !plot.hasPermission(getUserFromPlayer(event.getPlayer()), permission)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Registry<Plot, Integer> pr = Factionals.getFactionals().getRegistry(Plot.class, Integer.class);

        Plot plot = pr.get(Plots.getLocationId(event.getBlock().getLocation()));

        if(plot == null) {
            return;
        }

        if(!plot.hasPermission(getUserFromPlayer(event.getPlayer()), PlotPermission.BUILD)) {
            event.setCancelled(true);
        }

        if(event.getBlock().getState() instanceof Container && !plot.hasPermission(getUserFromPlayer(event.getPlayer()), PlotPermission.CONTAINER)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Registry<Plot, Integer> pr = Factionals.getFactionals().getRegistry(Plot.class, Integer.class);

        Plot plot = pr.get(Plots.getLocationId(event.getBlock().getLocation()));

        if(plot == null) {
            return;
        }

        if(!plot.hasPermission(getUserFromPlayer(event.getPlayer()), PlotPermission.BUILD)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Registry<Plot, Integer> pr = Factionals.getFactionals().getRegistry(Plot.class, Integer.class);

        InventoryHolder holder = event.getInventory().getHolder();

        Plot plot = null;

        if(holder instanceof Entity && !(holder instanceof HumanEntity)) {
            plot = pr.get(Plots.getLocationId(((Entity) holder).getLocation()));
        }

        if(holder instanceof BlockState) {
            plot = pr.get(Plots.getLocationId(((BlockState) holder).getLocation()));
        }

        if(plot != null) {
            if(!plot.hasPermission(Factionals.getFactionals().getRegistry(User.class, UUID.class).get(event.getPlayer().getUniqueId()), PlotPermission.CONTAINER)) {
                event.setCancelled(true);
            }
        }
    }
}
