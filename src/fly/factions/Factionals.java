package fly.factions;

import fly.factions.menus.Menu;
import fly.factions.model.Faction;
import fly.factions.model.User;
import fly.factions.serialization.FactionSerializer;
import fly.factions.serialization.Serializer;
import fly.factions.serialization.UserSerializer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class Factionals extends JavaPlugin implements Listener {
    private Economy economy;

    private static Factionals FACTIONALS;
    private Logger logger = Bukkit.getLogger();

    private Map<String, Faction> factions = new HashMap<>();
    private Map<Integer, Faction> plots = new HashMap<>();
    private Map<UUID, User> users = new HashMap<>();

    private FactionSerializer factionSerializer = new FactionSerializer();
    private UserSerializer userSerializer = new UserSerializer();

    public Factionals() {
        FACTIONALS = this;
    }

    public static Factionals getFactionals() {
        return FACTIONALS;
    }

    public User getUserFromPlayer(Player player) {
        return getUserFromUUID(player.getUniqueId());
    }

    public User getUserFromName(String player) {
        return getUserFromUUID(Bukkit.getPlayerUniqueId(player));
    }

    public User getUserFromUUID(UUID player) {
        return users.get(player);
    }

    public void addFaction(Faction faction) {
        factions.put(faction.getName(), faction);
    }

    public Faction getFactionByName(String string) {
        return factions.get(string);
    }

    @Override
    public void onEnable() {
        System.out.println(new File("").getAbsolutePath());

        logger.info(ChatColor.DARK_AQUA + "---------------------------------------------");
        logger.info(ChatColor.AQUA + "Starting Factionals!");
        logger.info(ChatColor.DARK_AQUA + "---------------------------------------------");

        Bukkit.getPluginManager().registerEvents(this, this);

        economy = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();

        Collection<User> userList = Serializer.loadAll(User.class);

        for(User user : userList) {
            users.put(user.getUuid(), user);
        }

        Collection<Faction> factionList = Serializer.loadAll(Faction.class);

        for(Faction faction : factionList) {
            factions.put(faction.getName(), faction);
        }

        Menu.init();
    }

    @Override
    public void onDisable() {
        Serializer.saveAll(factions.values());
        Serializer.saveAll(users.values());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Menu.openMenu((Player) sender, "main-menu");
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(getUserFromPlayer(event.getPlayer()) == null) {
            users.put(event.getPlayer().getUniqueId(), new User(event.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null) {
            return;
        }

        PersistentDataContainer container = event.getCurrentItem().getItemMeta().getPersistentDataContainer();

        if(container.has(Menu.MENU_NAMESPACE, PersistentDataType.STRING)) {
            Menu.buttonClicked(event, container.get(Menu.MENU_NAMESPACE, PersistentDataType.STRING));
        }
    }

    @EventHandler
    public void onChatUse(AsyncPlayerChatEvent event) {
        getUserFromPlayer(event.getPlayer()).onChat(event);
    }
}
