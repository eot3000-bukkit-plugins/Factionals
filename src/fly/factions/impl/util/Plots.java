package fly.factions.impl.util;

import fly.factions.Factionals;
import fly.factions.api.commands.CommandRegister;
import fly.factions.api.model.*;
import fly.factions.api.permissions.FactionPermission;
import fly.factions.api.permissions.Permissibles;
import fly.factions.api.permissions.PlotPermission;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class Plots {
    static {
        try {
            getXMask = binaryToInteger("00000000000000000000111111111111");
            getZMask = binaryToInteger("00000000111111111111000000000000");
            getWMask = binaryToInteger("11111111000000000000000000000000");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getXMask;
    public static int getZMask;
    public static int getWMask;

    private static Pattern permType = Pattern.compile("[adfpr]");

    private static int binaryToInteger(String binary) {
        char[] numbers = binary.toCharArray();
        int result = 0;
        for(int i=numbers.length - 1; i>=0; i--)
            if(numbers[i]=='1')
                result += Math.pow(2, (numbers.length-i - 1));
        return result;
    }

    public static Integer getLocationId(Location location) {
        return getLocationId(location.getChunk());
    }

    public static Integer getLocationId(Chunk chunk) {
        return getLocationId(chunk.getX(), chunk.getZ(), chunk.getWorld());
    }

    public static Integer getLocationId(int x, int z, World world) {
        return ((x+2048) | ((z+2048) << 12)) | (getWorldId(world) << 24);
    }

    public static Integer getLocationId(int x, int z, int world) {
        return ((x+2048) | ((z+2048) << 12)) | (world << 24);
    }

    public static int getX(int location) {
        return (location & getXMask)-2048;
    }

    public static int getZ(int location) {
        return ((location & getZMask) >> 12)-2048;
    }

    public static int getW(int location) {
        return (location & getWMask) >> 24;
    }

    //TODO: Split into another class

    private static Factionals factionals = Factionals.getFactionals();

    private static void requirePlotOwner(User user, Plot plot) {
        if(!plot.getOwner().userHasPlotPermissions(user, true, false)) {
            user.sendMessage(ChatColor.RED + "You do not own this plot");
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

    public static boolean plotForSale(CommandSender sender, String a, Integer price) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());
        Plot plot = factionals.getRegistry(Plot.class, Integer.class).get(getLocationId(((Player) sender).getLocation()));

        requireNotNull(plot, ChatColor.RED + "You are not in a plot", sender);
        requirePlotOwner(user, plot);

        plot.setPrice(Math.abs(price));

        return true;
    }

    public static boolean plotNotForSale(CommandSender sender, String a) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());
        Plot plot = factionals.getRegistry(Plot.class, Integer.class).get(getLocationId(((Player) sender).getLocation()));

        requireNotNull(plot, ChatColor.RED + "You are not in a plot", sender);
        requirePlotOwner(user, plot);

        plot.setPrice(-1);

        return true;
    }

    public static boolean buyPlot(CommandSender sender, String a) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());
        Plot plot = factionals.getRegistry(Plot.class, Integer.class).get(getLocationId(((Player) sender).getLocation()));

        requireNotNull(plot, ChatColor.RED + "You are not in a plot", sender);

        if(plot.getPrice() == -1) {
            sender.sendMessage(ChatColor.RED + "Plot is not for sale!");
            return false;
        }

        if(user.getBalance() < plot.getPrice()) {
            sender.sendMessage(ChatColor.RED + "Balance too low");
        } else {
            user.takeFromBalance(plot.getPrice());
            plot.getOwner().addToBalance(plot.getPrice());
            plot.setPrice(-1);
            plot.setOwner(user);
            return true;
        }

        return false;
    }

    public static boolean setRegion(CommandSender sender, String a, String region) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());
        Plot plot = factionals.getRegistry(Plot.class, Integer.class).get(getLocationId(((Player) sender).getLocation()));

        requireNotNull(plot, ChatColor.RED + "You are not in a plot", sender);
        requirePermission(user, FactionPermission.INTERNAL_MANAGEMENT, plot.getFaction());

        Region factionRegion = plot.getFaction().getRegion(region);

        requireNotNull(factionRegion, ChatColor.RED + "No such region", sender);

        plot.setAdministrator(factionRegion);

        return true;
    }

    public static boolean setPerm(CommandSender sender, String a, String b, String permissible, String permission, boolean on) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());
        Plot plot = factionals.getRegistry(Plot.class, Integer.class).get(Plots.getLocationId(((Player) sender).getLocation()));

        requireNotNull(plot, ChatColor.RED + "Not in a plot", sender);

        String permissibleType = "a";
        String permissible2 = permissible;


        if(permissible.charAt(1) == ':') {
            permissibleType = permissible.split(":")[0];
            permissible = permissible.split(":")[1];
        }

        if(!permType.matcher(permissibleType).matches()) {
            sender.sendMessage(ChatColor.RED + "The class selector " + ChatColor.YELLOW + permissibleType + ":" + ChatColor.RED + " is invalid! Please use a a valid selector");
            throw new CommandRegister.ReturnNowException();
        }

        List<Permissible> result = Permissibles.get(permissible);

        for(Permissible r : new ArrayList<>(result)) {
            if(permissibleType.equalsIgnoreCase("a")) {
                break;
            }

            if(permissibleType.equalsIgnoreCase("f") && !(r instanceof Faction)) {
                result.remove(r);
                continue;
            }

            if(permissibleType.equalsIgnoreCase("p") && !(r instanceof User)) {
                result.remove(r);
                continue;
            }

            if(permissibleType.equalsIgnoreCase("r") && !(r instanceof Region)) {
                result.remove(r);
                continue;
            }

            if(permissibleType.equalsIgnoreCase("d") && !(r instanceof ExecutiveDivision)) {
                result.remove(r);
            }
        }

        new CommandRegister.NumberPotential(1)
                .less((x) -> {
                    sender.sendMessage(ChatColor.RED + "No valid object is called " + ChatColor.YELLOW + permissible2);
                    throw new CommandRegister.ReturnNowException();
                })
                .equal((x) -> {
                    requirePlotOwner(user, plot);
                })
                .more((x) -> {
                    sender.sendMessage(ChatColor.YELLOW + permissible2 + ChatColor.RED + " is ambiguous! Please specify the permissible type using '<object type>:<object name>' instead");
                    throw new CommandRegister.ReturnNowException();
                }).run(result.size());

        try {
            PlotPermission plotPermission = PlotPermission.valueOf(permission);

            plot.setPermission(result.get(0), plotPermission, on);

            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static int getWorldId(World world) {
        switch (world.getName()) {
            case "world":
                return 0;

            case "world_nether":
                return -1;

            case "world_the_end":
                return 1;

            default:
                System.out.println(world.getName());
                return 100;
        }
    }

    public static World getWorld(int worldId) {
        switch (worldId) {
            case 0:
                return Bukkit.getWorld("world");

            case -1:
                return Bukkit.getWorld("world_nether");

            case 1:
                return Bukkit.getWorld("world_the_nether");

            default:
                System.out.println(worldId);
                return null;
        }
    }
}
