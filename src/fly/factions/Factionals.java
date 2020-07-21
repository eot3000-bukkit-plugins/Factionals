package fly.factions;

import fly.factions.commands.FactionCommand;
import fly.factions.commands.CompanyCommand;
import fly.factions.commands.PlotCommand;
import fly.factions.model.*;
import fly.factions.utils.FactionalsListener;
import fly.factions.utils.FileDataUtils;
import fly.factions.villagers.VillagerInfo;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Factionals extends JavaPlugin implements Listener {
    private static Factionals factionals;

    private Map<UUID, User> users = new HashMap<>();
    private Map<String, PlayerGroup> groups = new HashMap<>();
    private Map<PlotLocation, Plot> plots = new HashMap<>();
    private Map<UUID, VillagerInfo> villagers = new HashMap<>();

    private CompanyCommand companyCommand;
    private FactionCommand factionCommand;
    private PlotCommand plotCommand;

    private FactionalsListener listener;

    private int villageTickTask;

    @Override
    public void onLoad() {
        /*try {
            Field field = EntityTypes.class.getDeclaredField("ba");
            Field mods = Field.class.getDeclaredField("modifiers");

            mods.setAccessible(true);
            mods.set(field, field.getModifiers() & ~Modifier.FINAL);

            field.setAccessible(true);
            field.set(EntityTypes.VILLAGER, (EntityTypes.b) FactionalsEntityVillager::new);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onEnable() {
        companyCommand = new CompanyCommand();
        factionCommand = new FactionCommand();
        plotCommand = new PlotCommand();

        listener = new FactionalsListener();

        Bukkit.getPluginCommand("c").setExecutor(companyCommand);
        Bukkit.getPluginCommand("company").setExecutor(companyCommand);

        Bukkit.getPluginCommand("f").setExecutor(factionCommand);
        Bukkit.getPluginCommand("faction").setExecutor(factionCommand);

        Bukkit.getPluginCommand("p").setExecutor(plotCommand);
        Bukkit.getPluginCommand("plot").setExecutor(plotCommand);

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(listener, this);

        /*villageTickTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for(World world : Bukkit.getWorlds()) {
                Collection<Location> list = BlockStorage.getAllBlocksOfType((SlimefunItemStack) SlimefunItems.VILLAGE_SIGN, world);

                for (Location location : list) {
                    if(BlockStorage.getLocationInfo(location, "valid") != null) {
                        HouseUtils.startFinding(location);
                    }
                }
            }
        }, 0, 100);*/

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
