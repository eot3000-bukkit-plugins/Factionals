package fly.factions;

import com.destroystokyo.paper.Title;
import fly.factions.menus.Menu;
import fly.factions.model.Faction;
import fly.factions.model.Plot;
import fly.factions.model.User;
import fly.factions.serialization.FactionSerializer;
import fly.factions.serialization.Serializer;
import fly.factions.serialization.UserSerializer;
import javafx.util.Pair;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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

    public List<Faction> getFactions() {
        return new ArrayList<>(factions.values());
    }

    public void deleteFaction(Faction faction) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.YELLOW + "THE FACTION " + ChatColor.GREEN + faction.getName() + ChatColor.YELLOW + " HAS BEEN DELETED");
        }

        factions.remove(faction.getName());
    }

    public void setPlot(int x, Faction f) {
        plots.put(x, f);
    }

    public Faction getPlotOwner(int x) {
        return plots.get(x);
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
        event.setCancelled(true);

        User user = getUserFromPlayer(event.getPlayer());
        String message = event.getMessage();

        if(user.isClaimMode()) {
            if(message.startsWith("c ")) {
                if(user.getFaction() != null && user.getFaction().getLeader().equals(user)) {
                    user.getFaction().processLandClaim(message.replaceFirst("c ", ""), event.getPlayer().getLocation());
                    return;
                }
            }
            if(message.startsWith("map ")) {
                try {
                    Faction userFaction = getUserFromPlayer(event.getPlayer()).getFaction();

                    List<Character> characters = new ArrayList<>(Arrays.asList('#', '&', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'));
                    Map<Faction, Character> factionCharacters = new HashMap<>();

                    String[] split = message.split(" ");

                    int height = Integer.parseInt(split[1])*2+1;
                    int width = Integer.parseInt(split[2])*2+1;

                    int xb = event.getPlayer().getLocation().getChunk().getX();
                    int zb = event.getPlayer().getLocation().getChunk().getZ();
                    World w = event.getPlayer().getLocation().getWorld();

                    int xm = (int) Math.floor(height/2);
                    int zm = (int) Math.floor(width/2);

                    List<String> ret = new ArrayList<>();

                    factionCharacters.put(null, '-');

                    for(int z = 0; z < height; z++) {
                        String line = "";

                        for(int x = 0; x < width; x++) {
                            int plotId = Plot.getLocationId((xb+x)-xm, (zb+z)-zm, w);
                            Faction faction = plots.get(plotId);
                            String chunkAddition;

                            if(xm == x && zm == z) {
                                chunkAddition = ChatColor.BLACK + "";
                            } else if(faction == null) {
                                chunkAddition = ChatColor.GRAY + "";
                            } else if(faction.equals(userFaction)) {
                                chunkAddition = ChatColor.GREEN + "";
                            } else {
                                chunkAddition = ChatColor.DARK_GRAY + "";
                            }

                            if(faction != null && !factionCharacters.containsKey(faction)) {
                                factionCharacters.put(faction, characters.get(0));
                                characters.remove(0);
                            }

                            chunkAddition+=factionCharacters.get(faction);

                            line+=chunkAddition;
                        }
                        ret.add(line);
                    }

                    for(String string : ret) {
                        event.getPlayer().sendMessage(string);
                    }

                    return;
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    //
                }
            }
        }

        event.setCancelled(false);

        user.onChat(event);
    }

    @EventHandler
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
            }
        }

        if(plotFaction == faction) {
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
            event.getPlayer().sendTitle(new Title(ChatColor.GREEN + "Entering " + ChatColor.YELLOW + factionTo.getName() + ChatColor.GREEN + "!", "", 5, 10, 5));
        }
    }
}
