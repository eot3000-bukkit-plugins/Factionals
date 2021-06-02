package fly.factions;

import fly.factions.api.model.Faction;
import fly.factions.api.model.Plot;
import fly.factions.api.model.User;
import fly.factions.api.registries.Registry;
import fly.factions.impl.commands.FactionCommand;
//import fly.factions.impl.listeners.ChatListener;
import fly.factions.impl.commands.PlotCommand;
import fly.factions.impl.dynmap.DynmapManager;
import fly.factions.impl.listeners.JoinLeaveListener;
//import fly.factions.impl.listeners.MenusListener;
import fly.factions.impl.listeners.PlotListener;
import fly.factions.impl.registries.RegistryImpl;
import fly.factions.impl.serialization.FactionSerializer;
import fly.factions.api.serialization.Serializer;
import fly.factions.impl.serialization.UserSerializer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerSet;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class Factionals extends JavaPlugin implements Listener {
    private Map<Class<?>, Registry> registries = new HashMap<>();

    private FactionCommand factionCommand;
    private PlotCommand plotCommand;

    private Economy economy;

    private static Factionals FACTIONALS;
    private Logger logger = Bukkit.getLogger();

    private MarkerSet markerSet;

    public Factionals() {
        FACTIONALS = this;
    }

    public static Factionals getFactionals() {
        return FACTIONALS;
    }


    @Override
    public void onEnable() {
        logger.info(ChatColor.DARK_AQUA + "---------------------------------------------");
        logger.info(ChatColor.AQUA + "Starting Factionals!");
        logger.info(ChatColor.DARK_AQUA + "---------------------------------------------");


        registries.put(Faction.class, new RegistryImpl<Faction, String>(Faction.class));
        registries.put(Serializer.class, new RegistryImpl<Serializer, Class>(Serializer.class));
        registries.put(User.class, new RegistryImpl<User, UUID>(User.class));
        registries.put(Plot.class, new RegistryImpl<Plot, Integer>(Plot.class));


        new FactionSerializer(this);
        new UserSerializer(this);


        new JoinLeaveListener();
        new PlotListener();
        //new ChatListener();
        //new MenusListener();


        new DynmapManager();


        economy = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();


        factionCommand = new FactionCommand(this);
        plotCommand = new PlotCommand(this);


        Collection<User> userList = Serializer.loadAll(User.class);

        for(User user : userList) {
            registries.get(User.class).set(user.getUniqueId(), user);
        }

        Collection<Faction> factionList = Serializer.loadAll(Faction.class);

        for(Faction faction : factionList) {
            registries.get(Faction.class).set(faction.getName(), faction);
        }



        /*int count = 0;

        for(int v = 0; v < 5; v++) {
            for(int s = 0; s < 6; s++) {
                for(int h = 0; h < 9; h++) {
                    double h2 = h/8.1;
                    double s2 = s/1.0;
                    double v2 = v/4.0;

                    ItemStack stack = new ItemStack(Material.LEATHER_CHESTPLATE);
                    LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();

                    meta.setColor(hsvToRgb(h2, s2, v2));
                    meta.getPersistentDataContainer().set(MenuType.MENU_NAMESPACE, PersistentDataType.STRING, "colors1");
                    meta.setDisplayName("" + count++);

                    stack.setItemMeta(meta);

                    colors.add(stack);
                }
            }
        }*/

        //TODO: FIX THIS HUJDsinjkfjKHJENUDIWQHG(OI#@HNFuiwh9832rhiuewdnsaio
    }

    private List<Faction> menuListable(User user) {
        return new ArrayList<>(getRegistry(Faction.class).list());
    }

    /*private OpenedMenu.Button menuButton(Material material, String name, String button) {
        return new OpenedMenu.Button((x, y) -> x.setMenu(((Registry<MenuType, String>) getRegistry(MenuType.class)).get(button).create(x))) {
            @Override
            public ItemStack getItem() {
                ItemStack stack = new ItemStack(material);
                ItemMeta meta = stack.getItemMeta();

                meta.setDisplayName(name);

                stack.setItemMeta(meta);

                return stack;
            }
        };
    }*/

    @SuppressWarnings("unchecked")
    public <V> Registry<V, ?> getRegistry(Class<V> clazz) {
        return (Registry<V, ?>) registries.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public <V, K> Registry<V, K> getRegistry(Class<V> clazz, Class<K> clazz2) {
        return (Registry<V, K>) registries.get(clazz);
    }

    @Override
    public void onDisable() {
        Serializer.saveAll(registries.get(Faction.class).list(), Faction.class);
        Serializer.saveAll(registries.get(User.class).list(), User.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return true;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {

    }

    /*@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        int x = Plot.getLocationId(event.getClickedBlock().getLocation());
        User user = getUserFromPlayer(event.getPlayer());
        Faction faction = user.getFaction();
        Faction plotFaction = plots.get(x);

        if(plotFaction == null) {
            return;
        } else {
            if(plotFaction.isDeleted()) {
                plots.remove(x);
                return;
            }
        }

        String permissionsList = plotFaction.getPlots().get(x).getKey();

        if(permissionsList.contains("u" + user.getUuid().toString()) || permissionsList.contains("f" + faction.getName()) || faction.hasPermission(user, Permission.USE_ALL)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Faction factionFrom = plots.get(Plot.getLocationId(event.getFrom()));
        Faction factionTo = plots.get(Plot.getLocationId(event.getTo()));

        if(factionFrom == factionTo) {
            return;
        }

        if(factionTo == null) {
            event.getPlayer().sendTitle(new Title(ChatColor.DARK_GREEN + "Entering Wilderness!", "", 5, 10, 5));
        } else {
            event.getPlayer().sendTitle(new Title(ChatColor.GREEN + "Entering " + ChatColor.YELLOW + factionTo.name() + ChatColor.GREEN + "!", "", 5, 10, 5));
        }
    }*/

    public Economy getEconomy() {
        return economy;
    }
}
