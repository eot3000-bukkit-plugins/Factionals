package fly.factions.impl.util;

import fly.factions.Factionals;
import fly.factions.api.commands.CommandRegister;
import fly.factions.api.model.*;
import fly.factions.api.permissions.FactionPermission;
import fly.factions.api.permissions.Permissibles;
import fly.factions.api.permissions.PlotPermission;
import fly.factions.impl.commands.FactionCommand;
import fly.factions.impl.model.LotImpl;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class LotCommands {
    private static Pattern permType = Pattern.compile("[adfpr]");

    private static Factionals factionals = Factionals.getFactionals();

    private static void requirePlotOwner(User user, Lot lot) {
        if(!lot.getOwner().userHasPlotPermissions(user, true, false)) {
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
        Plot plot = factionals.getRegistry(Plot.class, Integer.class).get(Plots.getLocationId(((Player) sender).getLocation()));

        requireNotNull(plot, ChatColor.RED + "You are not in a lot", sender);

        Lot lot = plot.getLot(((Player) sender).getLocation());

        requireNotNull(lot, ChatColor.RED + "You are not in a lot", sender);

        requirePlotOwner(user, lot);

        lot.setPrice(Math.abs(price));

        return true;
    }

    public static boolean info(CommandSender sender, String a) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());
        Plot plot = factionals.getRegistry(Plot.class, Integer.class).get(Plots.getLocationId(((Player) sender).getLocation()));

        requireNotNull(plot, ChatColor.RED + "You are not in a plot", sender);

        Lot lot = plot.getLot(((Player) sender).getLocation());

        user.sendMessage(ChatColor.RED + "------Plot Info------");
        user.sendMessage(ChatColor.DARK_AQUA + "X/Z: " + ChatColor.AQUA + Plots.getX(plot.getLocationId()) + "/" + Plots.getZ(plot.getLocationId()));
        user.sendMessage(ChatColor.DARK_AQUA + "Administrator: " + ChatColor.AQUA + plot.getAdministrator().getName() +
                (plot.getAdministrator() instanceof Region ? ChatColor.DARK_AQUA + " (Region)" : ""));
        user.sendMessage(ChatColor.DARK_AQUA + "Faction: " + ChatColor.AQUA + plot.getFaction().getName());
        user.sendMessage("");

        if(lot != null) {
            user.sendMessage(ChatColor.RED + "------Lot Info------");
            user.sendMessage(ChatColor.DARK_AQUA + "Id: " + ChatColor.AQUA + lot.getId());
            user.sendMessage(ChatColor.DARK_AQUA + "Owner: " + ChatColor.AQUA + lot.getOwner().getName());
            user.sendMessage(ChatColor.DARK_AQUA + "Price: " + ChatColor.AQUA + (lot.getPrice() < 0 ? "Not for sale" : lot.getPrice()));
        }

        return true;
    }

    public static boolean plotNotForSale(CommandSender sender, String a) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());
        Plot plot = factionals.getRegistry(Plot.class, Integer.class).get(Plots.getLocationId(((Player) sender).getLocation()));

        requireNotNull(plot, ChatColor.RED + "You are not in a lot", sender);

        Lot lot = plot.getLot(((Player) sender).getLocation());

        requireNotNull(lot, ChatColor.RED + "You are not in a lot", sender);

        requirePlotOwner(user, lot);

        lot.setPrice(-1);

        return true;
    }

    public static boolean buyPlot(CommandSender sender, String a) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());
        Plot plot = factionals.getRegistry(Plot.class, Integer.class).get(Plots.getLocationId(((Player) sender).getLocation()));

        requireNotNull(plot, ChatColor.RED + "You are not in a lot", sender);

        Lot lot = plot.getLot(((Player) sender).getLocation());

        requireNotNull(lot, ChatColor.RED + "You are not in a lot", sender);

        if(lot.getPrice() == -1) {
            sender.sendMessage(ChatColor.RED + "Plot is not for sale!");
            return false;
        }

        if(user.getBalance() < lot.getPrice()) {
            sender.sendMessage(ChatColor.RED + "Balance too low");
        } else {
            user.takeFromBalance(lot.getPrice());
            lot.getOwner().addToBalance(lot.getPrice());
            lot.setPrice(-1);
            lot.setOwner(user);
            return true;
        }

        return false;
    }

    public static boolean setPerm(CommandSender sender, String a, String b, String permissible, String permission, boolean on) {
        CommandRegister.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());
        Plot plot = factionals.getRegistry(Plot.class, Integer.class).get(Plots.getLocationId(((Player) sender).getLocation()));

        requireNotNull(plot, ChatColor.RED + "You are not in a lot", sender);

        Lot lot = plot.getLot(((Player) sender).getLocation());

        requireNotNull(lot, ChatColor.RED + "You are not in a lot", sender);

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
                    requirePlotOwner(user, lot);
                })
                .more((x) -> {
                    sender.sendMessage(ChatColor.YELLOW + permissible2 + ChatColor.RED + " is ambiguous! Please specify the permissible type using '<object type>:<object name>' instead");
                    throw new CommandRegister.ReturnNowException();
                }).run(result.size());

        try {
            PlotPermission plotPermission = PlotPermission.valueOf(permission);

            lot.setPermission(result.get(0), plotPermission, on);

            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean setLot(CommandSender sender, String a, String b, Integer x1, Integer z1, Integer x2, Integer z2, Integer lot, String region) {
        FactionCommand.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        int xL = Math.min(x1, x2);
        int zL = Math.min(z1, z2);

        int xG = Math.max(x1, x2);
        int zG = Math.max(z1, z2);

        World world = ((Player) sender).getWorld();

        Faction faction = factionals.getRegistry(Plot.class, Integer.class).get(Plots.getLocationId(new Location(world, x1, 0, z1))).getFaction();

        if(faction.getRegion(region) == null) {
            return false;
        }

        if(!faction.getRegion(region).getLeader().equals(user) && !faction.getLeader().equals(user)) {
            return false;
        }

        for(int x = xL; x <= xG; x++) {
            for(int z = zL; z <= zG; z++) {
                Plot plot = factionals.getRegistry(Plot.class, Integer.class).get(Plots.getLocationId(new Location(world, x, 0, z)));
                LandAdministrator admin = plot.getAdministrator();

                if(faction != plot.getFaction() || !(admin instanceof Region && admin.getName().equalsIgnoreCase(region))) {
                    return false;
                }
            }
        }

        Lot lotObject = null;

        for(int x = xL; x <= xG; x++) {
            for(int z = zL; z <= zG; z++) {
                Plot plot = factionals.getRegistry(Plot.class, Integer.class).get(Plots.getLocationId(new Location(world, x, 0, z)));

                plot.setLot(new Location(world, x, 0, z), lotObject == null ? lotObject = ((Region) plot.getAdministrator()).getLots().get(lot) : lotObject);
            }
        }

        return true;
    }

    public static boolean setTown(CommandSender sender, String a, String b, String region, String town, Integer lot) {
        FactionCommand.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());

        Region factionRegion = user.getFaction().getRegion(region);

        //TODO: null check

        Lot factionLot = factionRegion.getLots().get(lot);

        //TODO: null check

        Town factionTown = factionRegion.getTown(town);

        //TODO: null check

        if(factionRegion.getFaction().hasPermission(user, FactionPermission.INTERNAL_MANAGEMENT) || factionRegion.getLeader().equals(user)) {
            factionLot.setTown(factionTown);
        }

        return false;
    }

    public static boolean createLot(CommandSender sender, String a, String b, String region) {
        FactionCommand.requirePlayer(sender);

        User user = factionals.getRegistry(User.class, UUID.class).get(((Player) sender).getUniqueId());
        World world = ((Player) sender).getWorld();
        Faction faction = user.getFaction();

        if(faction.getRegion(region) == null) {
            return false;
        }

        if(!faction.getRegion(region).getLeader().equals(user) && !faction.getLeader().equals(user)) {
            return false;
        }

        Region factionRegion = faction.getRegion(region);

        factionRegion.setLot(factionRegion.getLots().size(), new LotImpl(factionRegion, factionRegion.getLots().size(), world));

        sender.sendMessage("" + (factionRegion.getLots().size()-1));
        return true;
    }
}
