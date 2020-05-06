package fly.factions.commands;

import fly.factions.Factionals;
import fly.factions.model.*;
import fly.factions.permissions.GroupPermission;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class PlotCommand extends FactionalsCommandExecutor {

    public PlotCommand() {
        registerSubCommand(2, this::claimPlotSelf, "claim", "self");
        registerSubCommand(3, this::claimPlotNation, "claim", "faction");
        registerSubCommand(3, this::claimPlotGroup, "claim", "group");
        registerSubCommand(2, this::plotFS, "fs");
        registerSubCommand(2, this::plotFS, "forsale");
        registerSubCommand(1, this::plotNFS, "nfs");
        registerSubCommand(1, this::plotNFS, "notforsale");
        registerSubCommand(3, this::permissionUser, "permission", "user");
        registerSubCommand(3, this::permissionFaction, "permission", "faction");
        registerSubCommand(3, this::permissionGroup, "permission", "group");
    }

    private boolean claimPlotSelf(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));

        if(plot != null && plot.isForSale()) {
            if(plot.sell(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
                info.executor.sendMessage(ChatColor.GREEN + "Success!");
                return true;
            } else {
                info.executor.sendMessage("Not enough money!");
                return false;
            }
        }

        info.executor.sendMessage(ChatColor.RED + "Plot not for sale!");
        return false;
    }


    private boolean claimPlotNation(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));
        PlayerGroup group = factionals.getGroupByName(info.args[2]);

        if(!(group instanceof Faction)) {
            info.executor.sendMessage(ChatColor.RED + "No such faction!");
            return false;
        }

        if (!group.hasPermission(GroupPermission.WITHDRAW, factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(ChatColor.RED + "No permission! Contact your faction owner if this is a mistake");
            return false;
        }

        if(plot != null && plot.isForSale()) {
            if (plot.sell(group)) {
                info.executor.sendMessage(ChatColor.GREEN + "Success!");
                return true;
            } else {
                info.executor.sendMessage(ChatColor.RED + "Not enough money!");
                return false;
            }
        }

        info.executor.sendMessage(ChatColor.RED + "Plot not for sale!");
        return false;
    }


    private boolean claimPlotGroup(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));
        PlayerGroup group = factionals.getGroupByName(info.args[2]);

        if(group == null) {
            info.executor.sendMessage(ChatColor.RED + "No such group!");
            return false;
        }

        if (!group.hasPermission(GroupPermission.WITHDRAW, factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(ChatColor.RED + "No permission! Contact your group owner if this is a mistake");
            return false;
        }

        if(plot != null && plot.isForSale()) {
            if (plot.sell(group)) {
                info.executor.sendMessage(ChatColor.GREEN + "Success!");
                return true;
            } else {
                info.executor.sendMessage(ChatColor.RED + "Not enough money!");
                return false;
            }
        }

        info.executor.sendMessage(ChatColor.RED + "Plot not for sale!");
        return false;
    }

    private boolean plotFS(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));

        if(plot == null) {
            info.executor.sendMessage(ChatColor.RED + "No plot here!");
            return false;
        }

        if(!plot.getOwner().doesOwnPlots(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(ChatColor.RED + "No permission! You must be the plot owner to do this!");
            return false;
        }

        try {
            plot.setForSale(true);
            plot.setCost(Integer.parseInt(info.args[1]));
            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        } catch (NumberFormatException e) {
            info.executor.sendMessage(ChatColor.RED + "Not an integer (number with no decimal)");
        }

        return false;
    }

    private boolean plotNFS(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));

        if(plot == null) {
            info.executor.sendMessage(ChatColor.RED + "No plot here!");
            return false;
        }

        if(!plot.getOwner().doesOwnPlots(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(ChatColor.RED + "No permission! You must be the plot owner to do this!");
            return false;
        }

        try {
            plot.setForSale(true);
            plot.setCost(0);
            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        } catch (NumberFormatException e) {
            info.executor.sendMessage(ChatColor.RED + "Not an integer (number with no decimal)!");
        }

        return false;
    }

    private boolean permissionUser(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));
        User victim = factionals.getUserByName(info.args[2]);
        Plot.PlotPermission permission;

        if(victim == null) {
            info.executor.sendMessage(ChatColor.RED + "No such user!");
            return false;
        }

        try {
            permission = Plot.PlotPermission.valueOf(info.args[3].toLowerCase().replaceAll("-", "_"));
        } catch (Exception e) {
            info.executor.sendMessage(ChatColor.RED + "Not an acceptable plot permission");
            return false;
        }

        if(plot == null) {
            info.executor.sendMessage(ChatColor.RED + "No plot here!");
            return false;
        }

        if(!plot.getOwner().doesOwnPlots(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(ChatColor.RED + "No permission! You must be the plot owner to do this!");
            return false;
        }

        if(plot.getPermissionsFor(victim) == null) {
            plot.getOrCreatePermission(victim).addPermission(permission);
            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        }
        return false;
    }

    private boolean permissionFaction(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));
        PlayerGroup victim = factionals.getGroupByName(info.args[2]);
        Plot.PlotPermission permission;

        if(!(victim instanceof Faction)) {
            info.executor.sendMessage(ChatColor.RED + "No such faction!");
            return false;
        }

        try {
            permission = Plot.PlotPermission.valueOf(info.args[3].toLowerCase().replaceAll("-", "_"));
        } catch (Exception e) {
            info.executor.sendMessage(ChatColor.RED + "Not an acceptable plot permission");
            return false;
        }

        if(plot == null) {
            info.executor.sendMessage(ChatColor.RED + "No plot here!");
            return false;
        }

        if(!plot.getOwner().doesOwnPlots(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(ChatColor.RED + "No permission! You must be the plot owner to do this!");
            return false;
        }

        if(plot.getPermissionsFor(victim) == null) {
            plot.getOrCreatePermission(victim).addPermission(permission);
            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        }
        return false;
    }

    private boolean permissionGroup(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));
        PlayerGroup victim = factionals.getGroupByName(info.args[2]);
        Plot.PlotPermission permission;

        if(victim == null) {
            info.executor.sendMessage(ChatColor.RED + "No such group!");
            return false;
        }

        try {
            permission = Plot.PlotPermission.valueOf(info.args[3].toLowerCase().replaceAll("-", "_"));
        } catch (Exception e) {
            info.executor.sendMessage(ChatColor.RED + "Not an acceptable plot permission");
            return false;
        }

        if(plot == null) {
            info.executor.sendMessage(ChatColor.RED + "No plot here!");
            return false;
        }

        if(!plot.getOwner().doesOwnPlots(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(ChatColor.RED + "No permission! You must be the plot owner to do this!");
            return false;
        }

        if(plot.getPermissionsFor(victim) == null) {
            plot.getOrCreatePermission(victim).addPermission(permission);
            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        }
        return false;
    }
}
