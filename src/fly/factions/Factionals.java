package fly.factions;

import fly.factions.commands.FactionCommand;
import fly.factions.model.*;
import fly.factions.utils.FileDataUtils;
import org.bukkit.Bukkit;
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
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Factionals extends JavaPlugin implements Listener {
    private static Factionals factionals;
    private static EnumSet<Material> vehicles = EnumSet.of(Material.ACACIA_BOAT, Material.BIRCH_BOAT, Material.DARK_OAK_BOAT, Material.JUNGLE_BOAT, Material.SPRUCE_BOAT, Material.MINECART);

    private Map<UUID, User> users = new HashMap<>();
    private Map<String, PlayerGroup> groups = new HashMap<>();
    private Map<PlotLocation, Plot> plots = new HashMap<>();

    private FactionCommand factionCommand;

    @Override
    public void onEnable() {
        factionCommand = new FactionCommand();

        Bukkit.getPluginCommand("f").setExecutor(factionCommand);
        Bukkit.getPluginCommand("faction").setExecutor(factionCommand);

        Bukkit.getPluginManager().registerEvents(this, this);

        FileDataUtils.loadUsers();
        FileDataUtils.loadGroups();
        FileDataUtils.loadPlots();
    }

    @Override
    public void onDisable() {
        FileDataUtils.saveGroups();
        FileDataUtils.savePlots();
        FileDataUtils.saveUsers();
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        users.computeIfAbsent(event.getPlayer().getUniqueId(), User::new);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerInteract(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) {
            return;
        }
        Plot plot = plots.get(new PlotLocation(event.getClickedBlock().getLocation()));
        User user = users.get(event.getPlayer().getUniqueId());

        if(plot != null) {
            for(PlotOwner owner : plot.getPermissibleObjects()) {
                if(owner.canDo(user) && plot.getPermissionsFor(user).getPermissions().contains(getPermission(event))) {
                    return;
                }
            }
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent event) {
        Plot plot = plots.get(new PlotLocation(event.getBlock().getLocation()));
        User user = users.get(event.getPlayer().getUniqueId());

        if(plot != null) {
            for(PlotOwner owner : plot.getPermissibleObjects()) {
                if(owner.canDo(user) && plot.getPermissionsFor(owner).getPermissions().contains(Plot.PlotPermission.BUILD)) {
                    return;
                }
            }
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void entityInteract(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) {
            return;
        }
        Plot plot = plots.get(new PlotLocation(event.getEntity().getLocation()));
        User user = users.get(event.getDamager().getUniqueId());

        if(plot != null) {
            for(PlotOwner owner : plot.getPermissibleObjects()) {
                if(owner.canDo(user) && plot.getPermissionsFor(owner).getPermissions().contains(Plot.PlotPermission.OTHER_INTERACT)) {
                    return;
                }
            }
        }

        event.setCancelled(true);
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


    //Users
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserByUUID(UUID uuid) {
        return users.get(uuid);
    }

    public User getUserByName(String name) {
        return users.get(Bukkit.getOfflinePlayer(name).getUniqueId());
    }

    public void addUser(User user) {
        users.put(user.getUuid(), user);
    }


    //Groups
    public List<PlayerGroup> getGroups() {
        return new ArrayList<>(groups.values());
    }

    public void addGroup(PlayerGroup group) {
        groups.put(group.getName().toLowerCase(), group);
    }

    public void removeGroup(String name) {
        groups.remove(name);
    }

    public PlayerGroup getGroupByName(String s) {
        return groups.get(s.toLowerCase());
    }


    //Plots
    public List<Plot> getPlots() {
        return new ArrayList<>(plots.values());
    }

    public void addPlot(Plot plot) {
        plots.put(plot.getLocation(), plot);
    }

    public void removePlot(PlotLocation plot) {
        plots.remove(plot);
    }

    public Plot getPlotByLocation(PlotLocation location) {
        return plots.get(location);
    }

    public Factionals() {
        factionals = this;
    }

    public static Factionals getFactionals() {
        return factionals;
    }
}
