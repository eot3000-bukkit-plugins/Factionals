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
        return getLocationId(x, z, getWorldId(world));
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

    public static int getWorldId(World world) {
        if(world == null) {
            System.out.println("uh oh");
            return 100;
        }

        switch (world.getName()) {
            case "world":
                return 0;

            case "world_nether":
                return 1;

            case "world_the_end":
                return 2;

            default:
                System.out.println(world.getName());
                return 100;
        }
    }

    public static World getWorld(int worldId) {
        World x = getWorld0(worldId);

        for(World world : Bukkit.getWorlds()) {
            System.out.println(world.getName());
        }

        System.out.println(worldId + " " + x.getName());

        return x;
    }

    public static World getWorld0(int worldId) {
        switch (worldId) {
            case 0:
                return Bukkit.getWorld("world");

            case 1:
                return Bukkit.getWorld("world_nether");

            case 2:
                return Bukkit.getWorld("world_the_end");

            default:
                System.out.println(worldId);
                return null;
        }
    }

    //TODO: Move commands into separate class

    private static Factionals factionals = Factionals.getFactionals();

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
}
