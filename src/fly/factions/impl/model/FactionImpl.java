package fly.factions.impl.model;

import fly.factions.Factionals;
import fly.factions.api.commands.CommandRegister;
import fly.factions.api.exceptions.NotAMemberException;
import fly.factions.api.model.*;
import fly.factions.api.permissions.FactionPermission;
import fly.factions.api.registries.Registry;
import fly.factions.impl.util.Plots;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class FactionImpl implements fly.factions.api.model.Faction {
    private User leader;
    private Set<User> members = new HashSet<>();
    private String name;
    private ItemStack banner;

    private boolean isDeleted;

    private List<ExecutiveDivision> departments = new ArrayList<>();
    private List<Region> regions = new ArrayList<>();
    private List<Plot> plots = new ArrayList<>();

    public FactionImpl(User leader, String string) {
        this.leader = leader;
        this.name = string;

        members.add(leader);
    }

    @Override
    public Collection<Plot> getPlots() {
        return new ArrayList<>(plots);
    }

    @Override
    public void addPlot(Plot plot) {
        plots.add(plot);
    }

    @Override
    public void removePlot(Plot plot) {
        plots.remove(plot);
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

    @Override
    public String getId() {
        return null;
    }

    @Override
    public ItemStack getItem() {
        return banner;
    }

    @Override
    public void broadcast(String s) {
        for(User user : members) {
            user.sendMessage(s);
        }
    }

    @Override
    public Collection<User> getMembers() {
        return new ArrayList<>(members);
    }

    @Override
    public User getLeader() {
        return leader;
    }

    @Override
    public void setLeader(User leader) {
        if(!members.contains(leader)) {
            throw new NotAMemberException();
        }

        this.leader = leader;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasPermission(User user, FactionPermission permission) {
        return true;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public void addMember(User user) {
        members.add(user);
    }

    @Override
    public void removeMember(User user) {
        if(user.equals(leader)) {
            delete();
            return;
        }

        members.remove(user);

        for(Region region : regions) {
            region.removeMember(user);
        }

        for(ExecutiveDivision division : departments) {
            division.removeMember(user);
        }
    }

    @Override
    public Collection<Region> getRegions() {
        return new ArrayList<>(regions);
    }

    @Override
    public Region getRegion(String s) {
        for(Region region : regions) {
            if (region.getName().equalsIgnoreCase(s)) {
                return region;
            }
        }

        return null;
    }

    @Override
    public void addRegion(Region region) {
        regions.add(region);
    }

    @Override
    public void removeRegion(Region region) {
        regions.remove(region);
    }

    @Override
    public Collection<ExecutiveDivision> getDepartments() {
        return new ArrayList<>(departments);
    }

    @Override
    public ExecutiveDivision getDepartment(String s) {
        for(ExecutiveDivision department : departments) {
            if (department.getName().equalsIgnoreCase(s)) {
                return department;
            }
        }

        return null;
    }

    @Override
    public void addDepartment(ExecutiveDivision division) {
        departments.add(division);
    }

    @Override
    public void removeDepartment(ExecutiveDivision division) {
        departments.remove(division);
    }

    @Override
    public void delete() {
        this.isDeleted = true;

        factionals.getRegistry(Faction.class, String.class).set(name, null);
    }

    private static void requireFactionNotExist(CommandSender sender, String name) {
        Faction faction = factionals.getRegistry(Faction.class, String.class).get(name);

        if(faction != null) {
            sender.sendMessage(ChatColor.RED + "Faction already exists!");
            throw new CommandRegister.ReturnNowException();
        }
    }

    private static void requireNotNull(Object o, String message, CommandSender sender) {
        if(o == null) {
            sender.sendMessage(message);
            throw new CommandRegister.ReturnNowException();
        }
    }

    private static void requirePermission(User user, FactionPermission permission, Faction faction) {
        if(!faction.hasPermission(user, permission)) {
            user.sendMessage(ChatColor.RED + "No permission");
            throw new CommandRegister.ReturnNowException();
        }
    }

    private static void requireRegionNotExist(CommandSender user, String s, Faction faction) {
        if(faction.getRegion(s) != null) {
            user.sendMessage(ChatColor.RED + "Region with that name already exists");
            throw new CommandRegister.ReturnNowException();
        }
    }

    private static void requireDepartmentNotExist(CommandSender user, String s, Faction faction) {
        if(faction.getDepartment(s) != null) {
            user.sendMessage(ChatColor.RED + "Department with that name already exists");
            throw new CommandRegister.ReturnNowException();
        }
    }

    public static boolean createFaction(CommandSender sender, String a, String name) {
        CommandRegister.requirePlayer(sender);
        requireFactionNotExist(sender, name);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        Faction faction = new FactionImpl(user, name);

        factionals.getRegistry(Faction.class, String.class).set(name, faction);
        user.setFaction(faction);
        return true;
    }

    public static boolean createRegion(CommandSender sender, String a, String s) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);
        requirePermission(user, FactionPermission.INTERNAL_MANAGEMENT, user.getFaction());
        requireRegionNotExist(sender, s, user.getFaction());

        user.getFaction().addRegion(new RegionImpl(s, user, user.getFaction()));
        return true;
    }

    public static boolean createDepartment(CommandSender sender, String a, String s) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);
        requirePermission(user, FactionPermission.INTERNAL_MANAGEMENT, user.getFaction());
        requireDepartmentNotExist(sender, s, user.getFaction());

        user.getFaction().addDepartment(new ExecutiveDivisionImpl(s, user, user.getFaction()));
        return true;
    }

    public static boolean claim(CommandSender sender, String a) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), "You are not in a faction!", sender);
        requirePermission(user, FactionPermission.TERRITORY, user.getFaction());

        Location location = ((Player) sender).getLocation();

        Plot plot = new PlotImpl(location.getChunk().getX(), location.getChunk().getZ(), location.getWorld(), user.getFaction());

        plot.setFaction(user.getFaction());

        return true;
    }

    public static boolean map(CommandSender sender, String a) {
        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        Faction userFaction = user.getFaction();

        List<Character> characters = new ArrayList<>(Arrays.asList('#', '&', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'));
        Map<Faction, Character> factionCharacters = new HashMap<>();

        int height = 10;
        int width = 5;

        int xb = ((Player) sender).getLocation().getChunk().getX();
        int zb = ((Player) sender).getLocation().getChunk().getZ();
        World w = ((Player) sender).getLocation().getWorld();

        int xm = (int) Math.floor(height/2);
        int zm = (int) Math.floor(width/2);

        List<String> ret = new ArrayList<>();

        factionCharacters.put(null, '-');

        for(int z = 0; z < height; z++) {
            String line = "";

            for(int x = 0; x < width; x++) {
                int plotId = Plots.getLocationId((xb+x)-xm, (zb+z)-zm, w);
                Faction faction = Factionals.getFactionals().getRegistry(Plot.class, Integer.class).get(plotId).getFaction();
                String chunkAddition;

                if(faction == null || faction.isDeleted()) {
                    faction = null;
                }

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
            sender.sendMessage(string);
        }

        return true;
    }

    public static boolean info(CommandSender sender, String a) {
        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        for(Region region : user.getFaction().getRegions()) {
            sender.sendMessage(region.getName());
        }

        sender.sendMessage();

        for(ExecutiveDivision division : user.getFaction().getDepartments()) {
            sender.sendMessage(division.getName());
        }

        sender.sendMessage();

        for(Plot plot : user.getFaction().getPlots()) {
            sender.sendMessage(plot.getFaction().getName());
        }

        return true;
    }

    public static boolean inviteAdd(CommandSender sender, String a, String b, User victim) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);
        requirePermission(user, FactionPermission.USERS, user.getFaction());

        victim.addInvite(user.getFaction());

        return true;
    }

    public static boolean join(CommandSender sender, String a, Faction faction) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        if(user.getInvites().contains(faction)) {
            user.setFaction(faction);
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have an invite to this faction!");
        }

        return false;
    }

    public static boolean addToDepartment(CommandSender sender, String a, String b, String department, User victim) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);

        ExecutiveDivision division = user.getFaction().getDepartment(department);

        if(!division.getLeader().equals(user)) {
            requirePermission(user, FactionPermission.OWNER, user.getFaction());
        }

        requireNotNull(division, ChatColor.RED + "No such division!", sender);

        if(victim.getFaction().equals(user.getFaction())) {
            division.addMember(victim);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Victim is not in the faction!");
        return false;
    }

    public static boolean setDepartmentLeader(CommandSender sender, String a, String b, String department, User victim) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);

        ExecutiveDivision division = user.getFaction().getDepartment(department);

        if(!division.getLeader().equals(user)) {
            requirePermission(user, FactionPermission.OWNER, user.getFaction());
        }

        requireNotNull(division, ChatColor.RED + "No such division!", sender);

        if(victim.getFaction().equals(user.getFaction())) {
            division.setLeader(victim);
            division.addMember(victim);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Victim is not in the faction!");
        return false;
    }
}
