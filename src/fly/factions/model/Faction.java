package fly.factions.model;
import fly.factions.Factionals;
import fly.factions.messages.Messages;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Faction extends PlayerGroup {
    private static Factionals factionals = Factionals.getFactionals();
    private Map<Integer, Pair<String, int[]>> plots = new HashMap<>();
    private ItemStack banner;
    private Map<String, Region> regions = new HashMap<>();

    public Faction(String name, User leader) {
        super(name, leader);

        members.add(leader);

        factionals.addFaction(this);
        leader.setFaction(this);
    }

    public Faction(String name, User leader, long millis) {
        super(name, leader, millis);

        members.add(leader);

        factionals.addFaction(this);
        leader.setFaction(this);
    }

    public static void startCreation(Player player, String s) {
        User user = factionals.getUserFromPlayer(player);

        user.flagChat(Faction::continueCreation);

        Bukkit.getScheduler().runTaskLater(factionals, (Runnable) player::closeInventory, 1);

        player.sendMessage(Messages.WRITE_FACTION_NAME_CHAT);
    }

    private static void continueCreation(String string, Player player) {
        User user = factionals.getUserFromPlayer(player);
        string = string.replaceAll(" ", "_");

        if (factionals.getFactionByName(string) == null) {
            if(user.getFaction() != null) {
                user.getFaction().removeMember(user);
            }

            new Faction(string, user);

            player.sendMessage(Messages.SUCCESS);
        } else {
            player.sendMessage(Messages.FACTION_NAME_TAKEN.replaceAll("%1", string));
        }
    }

    public void tellEveryone(String message) {
        for(User user : members) {
            Player player = Bukkit.getPlayer(user.getUuid());

            if(player != null) {
                player.sendMessage(message);
            }
        }
    }

    public boolean hasPermission(User user, Permission perm) {
        return leader.equals(user);
    }

    public ItemStack getBanner() {
        return banner == null ? new ItemStack(Material.WHITE_BANNER) : banner;
    }

    public void setBanner(ItemStack banner) {
        this.banner = banner;
    }

    public Map<String, Region> getRegions() {
        return new HashMap<>(regions);
    }

    public void addRegion(Region region) {
        regions.put(region.getName(), region);
    }

    public void setPlot(int location, Pair<String, int[]> data) {
        plots.put(location, data);
    }

    public void processLandClaim(String args, Location location) {
        if(args.startsWith("o")) {
            int i = Plot.getLocationId(location);
            int[] perms = new int[32];

            perms[0] = 1;

            factionals.setPlot(i, this);
            plots.put(i, new Pair<>("f" + name + ";", perms));
        }

        if(args.startsWith("r")) {
            int i = Plot.getLocationId(location);
            Region region = regions.get(args.split(" ")[1]);

            if (factionals.getPlotOwner(i).equals(this)) {
                Pair<String, int[]> plotInfo = plots.get(i);
                Pair<String, int[]> newInfo = new Pair<>(region.getName() + " " + a(plotInfo.getKey()), plotInfo.getValue());

                plots.put(i, newInfo);
            }
        }
    }

    //Simple method to make the faction correctly formatted so that stuff won't break
    private String a(String stuff) {
        String[] split = stuff.split(";")[0].split(" ");

        if(split.length > 1) {
            return split[1];
        }

        return split[0];
    }

    public Map<Integer, Pair<String, int[]>> getPlots() {
        return new HashMap<>(plots);
    }

    @Override
    public double getMoney() {
        return 0;
    }

    @Override
    public void setMoney(double x) {

    }

    @Override
    public void addMoney(double x) {

    }

    @Override
    public void takeMoney(double x) {

    }
}
