package fly.factions.commands;

import fly.factions.model.*;
import fly.factions.permissions.GroupPermission;
import fly.factions.permissions.GroupRank;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class PlotCommand extends FactionalsCommandExecutor {

    public PlotCommand() {
        registerSubCommand(2, this::claimPlotSelf, "claim", "self");

        registerSubCommand(3, this::claimPlotNation, "claim", "faction")
            .also(3, notAFaction, pGroupFaction)
            .groupPermission(3, GroupPermission.WITHDRAW);

        registerSubCommand(3, this::claimPlotGroup, "claim", "company")
                .also(3, notAGroup, pGroupCompany)
                .groupPermission(3, GroupPermission.WITHDRAW);

        registerSubCommand(4, this::claimPlotRank, "claim", "rank")
                .also(3, notAGroup, pGroupNotNull);

        registerSubCommand(2, this::plotFS, "fs");

        registerSubCommand(2, this::plotFS, "forsale");

        registerSubCommand(1, this::plotNFS, "nfs");

        registerSubCommand(1, this::plotNFS, "notforsale");

        registerSubCommand(4, this::permissionUser, "permission", "user")
                .also(3, notAUser, pPlayerNotNull)
                .also(4, notAPermission, pPlotPermission);

        registerSubCommand(4, this::permissionFaction, "permission", "faction")
                .also(3, notAUser, pGroupFaction)
                .also(4, notAPermission, pPlotPermission);

        registerSubCommand(4, this::permissionGroup, "permission", "company")
                .also(3, notAUser, pGroupCompany)
                .also(4, notAPermission, pPlotPermission);

        /*registerSubCommand(5, this::permissionRank, "permission", "rank")
                .also(3, notAUser, pGroupNotNull)
                .also(4, notAPermission, pPlotPermission);*/
    }

    private boolean claimPlotSelf(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));

        if(plot != null && plot.isForSale()) {
            if(plot.sell(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
                info.executor.sendMessage(success);
                return true;
            } else {
                info.executor.sendMessage(notEnoughMoney);
                return false;
            }
        }

        info.executor.sendMessage(plotNotForSale);
        return false;
    }


    private boolean claimPlotNation(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));
        PlayerGroup group = factionals.getGroupByName(info.args[2]);

        if(plot != null && plot.isForSale()) {
            if (plot.sell(group)) {
                info.executor.sendMessage(success);
                return true;
            } else {
                info.executor.sendMessage(notEnoughMoney);
                return false;
            }
        }

        info.executor.sendMessage(plotNotForSale);
        return false;
    }


    private boolean claimPlotGroup(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));
        PlayerGroup group = factionals.getGroupByName(info.args[2]);

        if(plot != null && plot.isForSale()) {
            if (plot.sell(group)) {
                info.executor.sendMessage(success);
                return true;
            } else {
                info.executor.sendMessage(notEnoughMoney);
                return false;
            }
        }

        info.executor.sendMessage(plotNotForSale);
        return false;
    }

    private boolean claimPlotRank(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));
        PlayerGroup group = factionals.getGroupByName(info.args[2]);
        GroupRank rank = group.getRank(info.args[3]);

        if(rank == null) {
            info.executor.sendMessage(notARank);
            return false;
        }

        if(!rank.isOwner(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(noPermission);
            return false;
        }

        if(plot != null && plot.isForSale()) {
            if (plot.sell(rank)) {
                info.executor.sendMessage(success);
                return true;
            } else {
                info.executor.sendMessage(notEnoughMoney);
                return false;
            }
        }

        info.executor.sendMessage(plotNotForSale);
        return false;
    }

    private boolean plotFS(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));

        if(plot == null) {
            info.executor.sendMessage(notAPlot);
            return false;
        }

        if(!plot.getOwner().isOwner(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(noPermission);
            return false;
        }

        try {
            plot.setForSale(true);
            plot.setCost(Integer.parseInt(info.args[1]));
            info.executor.sendMessage(success);
            return true;
        } catch (NumberFormatException e) {
            info.executor.sendMessage(notAnInt);
        }

        return false;
    }

    private boolean plotNFS(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));

        if(plot == null) {
            info.executor.sendMessage(notAPlot);
            return false;
        }

        if(!plot.getOwner().isOwner(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(noPermission);
            return false;
        }

        try {
            plot.setForSale(true);
            plot.setCost(0);
            info.executor.sendMessage(success);
            return true;
        } catch (NumberFormatException e) {
            info.executor.sendMessage(notAnInt);
        }

        return false;
    }

    private boolean permissionUser(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));
        User victim = factionals.getUserByName(info.args[2]);
        Plot.PlotPermission permission = Plot.PlotPermission.valueOf(info.args[3].toUpperCase().replaceAll("-", "_"));

        if(plot == null) {
            info.executor.sendMessage(notAPlot);
            return false;
        }

        if(!plot.getOwner().isOwner(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(noPermission);
            return false;
        }

        if(plot.getPermissionsFor(victim) == null) {
            plot.getOrCreatePermission(victim);
        }
        plot.getPermissionsFor(victim).addPermission(permission);
        info.executor.sendMessage(success);
        return true;
    }

    private boolean permissionFaction(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));
        PlayerGroup victim = factionals.getGroupByName(info.args[2]);
        Plot.PlotPermission permission = Plot.PlotPermission.valueOf(info.args[3].toUpperCase().replaceAll("-", "_"));

        if(plot == null) {
            info.executor.sendMessage(notAPlot);
            return false;
        }

        if(!plot.getOwner().isOwner(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(noPermission);
            return false;
        }

        if(plot.getPermissionsFor(victim) == null) {
            plot.getOrCreatePermission(victim);
        }
        plot.getPermissionsFor(victim).addPermission(permission);
        info.executor.sendMessage(success);
        return true;
    }

    private boolean permissionGroup(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));
        PlayerGroup victim = factionals.getGroupByName(info.args[2]);
        Plot.PlotPermission permission = Plot.PlotPermission.valueOf(info.args[3].toUpperCase().replaceAll("-", "_"));

        if(plot == null) {
            info.executor.sendMessage(notAPlot);
            return false;
        }

        if(!plot.getOwner().isOwner(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(noPermission);
            return false;
        }

        if(plot.getPermissionsFor(victim) == null) {
            plot.getOrCreatePermission(victim);
        }
        plot.getPermissionsFor(victim).addPermission(permission);
        info.executor.sendMessage(success);
        return true;
    }

    private boolean permissionRank(CommandInfo info) {
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();
        Plot plot = factionals.getPlotByLocation(new PlotLocation(chunk));
        PlayerGroup group = factionals.getGroupByName(info.args[2]);
        Plot.PlotPermission permission = Plot.PlotPermission.valueOf(info.args[4].toUpperCase().replaceAll("-", "_"));
        GroupRank victim = group.getRank(info.args[3]);

        if(victim == null) {
            info.executor.sendMessage(notARank);
            return false;
        }

        if(plot == null) {
            info.executor.sendMessage(notAPlot);
            return false;
        }

        if(!plot.getOwner().isOwner(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(noPermission);
            return false;
        }

        if(plot.getPermissionsFor(victim) == null) {
            plot.getOrCreatePermission(victim);
        }
        plot.getPermissionsFor(victim).addPermission(permission);
        info.executor.sendMessage(success);
        return true;
    }

}
