package fly.factions;

import fly.factions.commands.FactionCommand;
import fly.factions.commands.GroupCommand;
import fly.factions.commands.PlotCommand;
import fly.factions.model.*;
import fly.factions.utils.FactionalsListener;
import fly.factions.utils.FileDataUtils;
import fly.factions.utils.HouseUtils;
import fly.factions.villagers.VillagerInfo;
import fly.factions.villagers.nms.entities.FactionalsEntityVillager;
import jdk.nashorn.internal.ir.Block;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import net.minecraft.server.v1_15_R1.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class Factionals extends JavaPlugin implements Listener {
    private static Factionals factionals;

    private Map<UUID, User> users = new HashMap<>();
    private Map<String, PlayerGroup> groups = new HashMap<>();
    private Map<PlotLocation, Plot> plots = new HashMap<>();
    private Map<UUID, VillagerInfo> villagers = new HashMap<>();

    private GroupCommand groupCommand;
    private FactionCommand factionCommand;
    private PlotCommand plotCommand;

    private FactionalsListener listener;

    private int villageTickTask;

    @Override
    public void onLoad() {
        try {
            Field field = EntityTypes.class.getDeclaredField("ba");
            Field mods = Field.class.getDeclaredField("modifiers");

            mods.setAccessible(true);
            mods.set(field, field.getModifiers() & ~Modifier.FINAL);

            field.setAccessible(true);
            field.set(EntityTypes.VILLAGER, (EntityTypes.b) FactionalsEntityVillager::new);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        groupCommand = new GroupCommand();
        factionCommand = new FactionCommand();
        plotCommand = new PlotCommand();

        listener = new FactionalsListener();

        Bukkit.getPluginCommand("g").setExecutor(groupCommand);
        Bukkit.getPluginCommand("group").setExecutor(groupCommand);

        Bukkit.getPluginCommand("f").setExecutor(factionCommand);
        Bukkit.getPluginCommand("faction").setExecutor(factionCommand);

        Bukkit.getPluginCommand("p").setExecutor(plotCommand);
        Bukkit.getPluginCommand("plot").setExecutor(plotCommand);

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(listener, this);

        villageTickTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for(World world : Bukkit.getWorlds()) {
                Collection<Location> list = BlockStorage.getAllBlocksOfType((SlimefunItemStack) SlimefunItems.VILLAGE_SIGN, world);

                for (Location location : list) {
                    if(BlockStorage.getLocationInfo(location, "valid") != null) {
                        HouseUtils.startFinding(location);
                    }
                }
            }
        }, 0, 100);

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

    //Villagers
    public VillagerInfo getVillagerInfo(UUID uuid) {
        return villagers.get(uuid);
    }

    public void addVillagerInfo(UUID uuid, VillagerInfo info) {
        villagers.put(uuid, info);
    }

    //
    public Factionals() {
        factionals = this;
    }

    public static Factionals getFactionals() {
        return factionals;
    }
}
