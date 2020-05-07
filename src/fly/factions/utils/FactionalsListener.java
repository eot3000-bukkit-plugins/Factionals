package fly.factions.utils;

import com.destroystokyo.paper.Title;
import fly.factions.Factionals;
import fly.factions.model.Plot;
import fly.factions.model.PlotLocation;
import fly.factions.model.PlotOwner;
import fly.factions.model.User;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Switch;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.EnumSet;

import static org.bukkit.ChatColor.*;

public class FactionalsListener implements Listener {
    Factionals factionals = Factionals.getFactionals();
    private static EnumSet<Material> vehicles = EnumSet.of(Material.ACACIA_BOAT, Material.BIRCH_BOAT, Material.DARK_OAK_BOAT, Material.JUNGLE_BOAT, Material.SPRUCE_BOAT, Material.MINECART);


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerInteract(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) {
            return;
        }
        Plot plot = factionals.getPlotByLocation(new PlotLocation(event.getClickedBlock().getLocation()));
        User user = factionals.getUserByUUID(event.getPlayer().getUniqueId());

        if(plot != null) {
            for(PlotOwner owner : plot.getPermissibleObjects()) {
                if(owner.canDo(user) && plot.getPermissionsFor(user).getPermissions().contains(getPermission(event))) {
                    return;
                }
            }
        } else {
            return;
        }

        event.setCancelled(!plot.getOwner().isOwner(user));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent event) {
        Plot plot = factionals.getPlotByLocation(new PlotLocation(event.getBlock().getLocation()));
        User user = factionals.getUserByUUID(event.getPlayer().getUniqueId());

        if(plot != null) {
            for(PlotOwner owner : plot.getPermissibleObjects()) {
                if(owner.canDo(user) && plot.getPermissionsFor(owner).getPermissions().contains(Plot.PlotPermission.BUILD)) {
                    return;
                }
            }
        } else {
            return;
        }

        event.setCancelled(!plot.getOwner().isOwner(user));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void entityInteract(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) {
            return;
        }
        Plot plot = factionals.getPlotByLocation(new PlotLocation(event.getEntity().getLocation()));
        User user = factionals.getUserByUUID(event.getDamager().getUniqueId());

        if(plot != null) {
            for(PlotOwner owner : plot.getPermissibleObjects()) {
                if(owner.canDo(user) && plot.getPermissionsFor(owner).getPermissions().contains(Plot.PlotPermission.OTHER_INTERACT)) {
                    return;
                }
            }
        }

        event.setCancelled(!plot.getOwner().isOwner(user));
    }


    private Plot.PlotPermission getPermission(PlayerInteractEvent event) {
        try {
            if (!event.getPlayer().isSneaking()) {
                if (event.getClickedBlock().getState() instanceof Container) {
                    return Plot.PlotPermission.CONTAINER;
                }
                if (event.getClickedBlock().getState() instanceof Switch) {
                    return Plot.PlotPermission.SWITCH;
                }
                if (event.getClickedBlock().getState() instanceof Door) {
                    return Plot.PlotPermission.DOOR;
                }
                if (event.getClickedBlock().getState() instanceof TrapDoor) {
                    return Plot.PlotPermission.TRAPDOOR;
                }
            }
            if (vehicles.contains(event.getItem().getType())) {
                return Plot.PlotPermission.VEHICLE;
            }
        } catch (NullPointerException e) {
            //
        }

        return Plot.PlotPermission.OTHER_INTERACT;
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        Plot p1 = factionals.getPlotByLocation(new PlotLocation(event.getFrom()));
        Plot p2 = factionals.getPlotByLocation(new PlotLocation(event.getTo()));

        if(p1 == p2) {
            return;
        }

        if(p2 == null) {
            event.getPlayer().sendTitle(new Title(DARK_GREEN + " Entering Wilderness", "", 10, 40, 10));
        } else {
            if(p1 != null && p1.getFaction().equals(p2.getFaction())) {
                event.getPlayer().sendMessage(GOLD + "Owner: " + YELLOW + p2.getOwner().niceName() + GREEN + (p2.isForSale() ? " [For sale: " + p2.getCost() + "]" : ""));
                return;
            }
            event.getPlayer().sendTitle(new Title(GOLD + " Entering " + p2.getFaction().getName()));

            event.getPlayer().sendMessage(GOLD + "Owner: " + YELLOW + p2.getOwner().niceName() + GREEN + (p2.isForSale() ? " [For sale: " + p2.getCost() + "]" : ""));
        }
    }
}
