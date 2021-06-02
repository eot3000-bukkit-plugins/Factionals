package fly.factions.impl.model;

import fly.factions.Factionals;
import fly.factions.api.commands.CommandRegister;
import fly.factions.api.exceptions.NotAMemberException;
import fly.factions.api.model.*;
import fly.factions.api.permissions.FactionPermission;
import fly.factions.api.permissions.Permissibles;
import fly.factions.api.serialization.Serializer;
import fly.factions.impl.util.Plots;
import javafx.util.Pair;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class FactionImpl extends AbstractLandAdministrator<Plot> implements Faction {
    private ItemStack banner = new ItemStack(Material.AIR);

    private long creationTime;

    private boolean isDeleted;

    private List<ExecutiveDivision> departments = new ArrayList<>();
    private List<Region> regions = new ArrayList<>();

    public FactionImpl(String name, User leader, long time) {
        super(name, leader);

        Permissibles.add(name, this);
        Permissibles.add(getId(), this);

        this.creationTime = time;
    }

    public FactionImpl(User leader, String name) {
        this(name, leader, System.currentTimeMillis());
    }

    //DYNMAP

    @Override
    public double getBorderOpacity() {
        return 0.9;
    }

    @Override
    public String getDesc() {
        return "<div class=\"regioninfo\"><div class=\"infowindow\"><span style=\"font-size:120%;\">" + name + "</span><br />" +
                "<span style=\"font-weight:bold;\">Leader:" + getLeader().getName() + "</span></div></div>";
    }

    //META

    @Override
    public String getId() {
        return "faction-" + getName();
    }

    @Override
    public ItemStack getItem() {
        return banner;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public void delete() {
        this.isDeleted = true;

        factionals.getRegistry(Faction.class, String.class).set(name, null);
        Serializer.saveAll(Collections.singletonList(this), Faction.class);

        Permissibles.remove(this);
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    //MEMBER MANAGEMENT

    @Override
    public void setLeader(User leader) {
        if(!members.contains(leader)) {
            throw new NotAMemberException();
        }

        this.leader = leader;
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

    //PERMISSIONS

    @Override
    public boolean hasPermission(User user, FactionPermission permission) {
        for(ExecutiveDivision division : departments) {
            if(division.getMembers().contains(user) && (division.canDo(permission) || division.canDo(FactionPermission.OWNER))) {
                return true;
            }
        }

        return leader.equals(user);
    }

    @Override
    public boolean userHasPlotPermissions(User user, boolean owner, boolean pub) {
        return owner ? user.equals(leader) : members.contains(user);
    }

    //REGIONS

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

    //DEPARTMENTS

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

    //TODO: Move commands into seperate class

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

    public static boolean claim(CommandSender sender, String a) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), "You are not in a faction!", sender);
        requirePermission(user, FactionPermission.TERRITORY, user.getFaction());

        Location location = ((Player) sender).getLocation();

        boolean result = claim0(location.getChunk().getX(), location.getChunk().getZ(), location.getWorld(), user.getFaction());

        if(!result) {
            sender.sendMessage(ChatColor.RED + "There is already a plot here");
        }

        return result;
    }

    private static boolean claim0(int x, int z, World world, Faction faction) {
        Plot old = factionals.getRegistry(Plot.class, Integer.class).get(Plots.getLocationId(x, z, world));

        if(old != null) {
            return false;
        }

        Plot plot = new PlotImpl(x, z, world, faction);

        plot.setFaction(faction);

        return true;
    }

    public static boolean claimFill(CommandSender sender, String a, String b) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), "You are not in a faction!", sender);
        requirePermission(user, FactionPermission.TERRITORY, user.getFaction());

        MutableInt m = new MutableInt(50);

        if(!claim0(((Player) sender).getLocation().getChunk().getX(), ((Player) sender).getLocation().getChunk().getZ(), ((Player) sender).getWorld(), user.getFaction())) {
            return true;
        }

        fillNode(((Player) sender).getLocation().getChunk().getX(), ((Player) sender).getLocation().getChunk().getZ(), ((Player) sender).getWorld(), user.getFaction(), m);

        return true;
    }

    private static void fillNode(int x, int z, World w, Faction faction, MutableInt left) {
        List<Pair<Integer, Integer>> list = new ArrayList<>();

        if(((int)left.getValue()) <= 0) {
            return;
        }

        if(claim0(x+1, z, w, faction)) {
            list.add(new Pair<>(x+1, z));
            left.setValue(((int) left.getValue())-1);
        }
        if(claim0(x, z+1, w, faction)) {
            list.add(new Pair<>(x, z+1));
            left.setValue(((int) left.getValue())-1);
        }
        if(claim0(x-1, z, w, faction)) {
            list.add(new Pair<>(x-1, z));
            left.setValue(((int) left.getValue())-1);
        }
        if(claim0(x, z-1, w, faction)) {
            list.add(new Pair<>(x, z-1));
            left.setValue(((int) left.getValue())-1);
        }

        for(Pair<Integer, Integer> pair : list) {
            fillNode(pair.getKey(), pair.getValue(), w, faction, left);
        }
    }

    public static boolean map(CommandSender sender, String a) {
        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        Faction userFaction = user.getFaction();

        List<Character> characters = new ArrayList<>(Arrays.asList('#', '&', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'));
        Map<Faction, Character> factionCharacters = new HashMap<>();

        int height = 11;
        int width = 25;

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
                Plot plot = Factionals.getFactionals().getRegistry(Plot.class, Integer.class).get(plotId);
                Faction faction = plot != null ? plot.getFaction() : null;
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

    public static boolean createRegion(CommandSender sender, String a, String b, String s) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);
        requirePermission(user, FactionPermission.INTERNAL_MANAGEMENT, user.getFaction());
        requireRegionNotExist(sender, s, user.getFaction());

        user.getFaction().addRegion(new RegionImpl(s, user, user.getFaction()));
        return true;
    }

    public static boolean addToRegion(CommandSender sender, String a, String b, String region, User victim) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);

        Region factionRegion = user.getFaction().getRegion(region);

        if(!factionRegion.getLeader().equals(user)) {
            requirePermission(user, FactionPermission.OWNER, user.getFaction());
        }

        requireNotNull(factionRegion, ChatColor.RED + "No such region!", sender);

        if(victim.getFaction().equals(user.getFaction())) {
            factionRegion.addMember(victim);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Victim is not in the faction!");
        return false;
    }

    public static boolean setRegionLeader(CommandSender sender, String a, String b, String c, String region, User victim) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);

        Region factionRegion = user.getFaction().getRegion(region);

        if(!factionRegion.getLeader().equals(user)) {
            requirePermission(user, FactionPermission.OWNER, user.getFaction());
        }

        requireNotNull(factionRegion, ChatColor.RED + "No such region!", sender);

        if(victim.getFaction().equals(user.getFaction())) {
            factionRegion.setLeader(victim);
            factionRegion.addMember(victim);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Victim is not in the faction!");
        return false;
    }

    public static boolean createDepartment(CommandSender sender, String a, String b, String s) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);
        requirePermission(user, FactionPermission.INTERNAL_MANAGEMENT, user.getFaction());
        requireDepartmentNotExist(sender, s, user.getFaction());

        user.getFaction().addDepartment(new ExecutiveDivisionImpl(s, user, user.getFaction()));
        return true;
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

    public static boolean setDepartmentLeader(CommandSender sender, String a, String b, String c, String department, User victim) {
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

    public static boolean setFactionFillColor(CommandSender sender, String a, String b, Integer red, Integer blue, Integer green) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);
        requirePermission(user, FactionPermission.OWNER, user.getFaction());

        user.getFaction().setFillColor(Color.fromRGB(red, green, blue));

        return true;
    }

    public static boolean setFactionFillOpacity(CommandSender sender, String a, String b, Double opacity) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);
        requirePermission(user, FactionPermission.OWNER, user.getFaction());

        user.getFaction().setFillOpacity(opacity);

        return true;
    }

    public static boolean setFactionBorderColor(CommandSender sender, String a, String b, Integer red, Integer blue, Integer green) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);
        requirePermission(user, FactionPermission.OWNER, user.getFaction());

        user.getFaction().setBorderColor(Color.fromRGB(red, green, blue));

        return true;
    }

    public static boolean setRegionFillColor(CommandSender sender, String a, String b, String c, String region, Integer red, Integer blue, Integer green) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);

        Region factionRegion = user.getFaction().getRegion(region);

        requireNotNull(factionRegion, ChatColor.RED + "No such region!", sender);

        if(!factionRegion.getLeader().equals(user)) {
            sender.sendMessage(ChatColor.RED + "No permission");
            return false;
        }

        factionRegion.setFillColor(Color.fromRGB(red, green, blue));

        return true;
    }

    public static boolean setRegionFillOpacity(CommandSender sender, String a, String b, String c, String region, Double opacity) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);

        Region factionRegion = user.getFaction().getRegion(region);

        requireNotNull(factionRegion, ChatColor.RED + "No such region!", sender);

        if(!factionRegion.getLeader().equals(user)) {
            sender.sendMessage(ChatColor.RED + "No permission");
            return false;
        }

        factionRegion.setFillOpacity(opacity);

        return true;
    }

    public static boolean setRegionBorderColor(CommandSender sender, String a, String b, String c, String region, Integer red, Integer blue, Integer green) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        requireNotNull(user.getFaction(), ChatColor.RED + "You are not in a faction!", sender);

        Region factionRegion = user.getFaction().getRegion(region);

        requireNotNull(factionRegion, ChatColor.RED + "No such region!", sender);

        if(!factionRegion.getLeader().equals(user)) {
            sender.sendMessage(ChatColor.RED + "No permission");
            return false;
        }

        factionRegion.setBorderColor(Color.fromRGB(red, green, blue));

        return true;
    }
}
