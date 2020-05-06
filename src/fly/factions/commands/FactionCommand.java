package fly.factions.commands;

import fly.factions.model.*;
import fly.factions.permissions.GroupPermission;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class FactionCommand extends FactionalsCommandExecutor {
    public FactionCommand() {
        registerSubCommand(2, this::createFaction, "create");
        registerSubCommand(4, this::grantCitizen, "citizenship", "grant");
        registerSubCommand(4, this::revokeCitizen, "citizenship", "revoke");
        registerSubCommand(2, this::claim, "claim");
        registerSubCommand(1, this::unclaim, "unclaim");
        registerSubCommand(5, this::rankCreate, "rank", "create");
        //registerSubCommand(5, this::rankPermission, "rank", "permission");
    }

    private boolean createFaction(CommandInfo info) {
        if(factionals.getGroupByName(info.args[1]) == null) {
            Faction faction = new Faction(factionals.getUserByUUID(((Player) info.executor).getUniqueId()), info.args[1]);

            factionals.addGroup(faction);
            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        }
        info.executor.sendMessage(ChatColor.RED + "Name already taken!");
        return false;
}

    private boolean grantCitizen(CommandInfo info) {
        PlayerGroup group = factionals.getGroupByName(info.args[2]);

        if(group instanceof Faction) {
            if(!group.hasPermission(GroupPermission.MEMBER_INVITE, factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
                info.executor.sendMessage(ChatColor.RED + "No permission! Contact your faction owner if this is a mistake");
                return false;
            }
            User victim = factionals.getUserByName(info.args[3]);
            if(victim == null) {
                info.executor.sendMessage(ChatColor.RED + "No such user!");
                return false;
            }
            if(!group.getMembers().contains(victim)) {
                info.executor.sendMessage(ChatColor.RED + "Victim already a member of your faction!");
                return false;
            }
            group.addMember(victim);
            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        }

        info.executor.sendMessage(ChatColor.RED + "Not a faction!");
        return false;
    }

    private boolean revokeCitizen(CommandInfo info) {
        PlayerGroup group = factionals.getGroupByName(info.args[2]);

        if(group instanceof Faction) {
            if(!group.hasPermission(GroupPermission.MEMBER_KICK, factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
                info.executor.sendMessage(ChatColor.RED + "No permission! Contact your faction owner if this is a mistake");
                return false;
            }
            User victim = factionals.getUserByName(info.args[3]);

            if(victim == null) {
                info.executor.sendMessage(ChatColor.RED + "No such user!");
                return false;
            }
            if(group.getMembers().contains(victim)) {
                info.executor.sendMessage(ChatColor.RED + "Victim not a member of faction!");
                return false;
            }
            group.removeMember(victim);
            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        }

        info.executor.sendMessage(ChatColor.RED + "Not a faction!");
        return false;
    }

    private boolean claim(CommandInfo info) {
        PlayerGroup group = factionals.getGroupByName(info.args[1]);

        if(group instanceof Faction) {
            if(!group.hasPermission(GroupPermission.LAND_CLAIM, factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
                info.executor.sendMessage(ChatColor.RED + "No permission! Contact your faction owner if this is a mistake");
                return false;
            }
            Plot plot = new Plot((Faction) group, group, new PlotLocation(((Player) info.executor).getLocation()));

            ((Faction) group).claim(plot);
            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        }

        info.executor.sendMessage(ChatColor.RED + "Not a faction!");
        return false;
    }

    private boolean unclaim(CommandInfo info) {
        PlayerGroup group = factionals.getGroupByName(info.args[1]);
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();

        if(group instanceof Faction) {
            if(!group.hasPermission(GroupPermission.LAND_UNCLAIM, factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
                info.executor.sendMessage(ChatColor.RED + "No permission! Contact your faction owner if this is a mistake");
                return false;
            }
            ((Faction) group).unclaim(new PlotLocation(chunk));
            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        }

        info.executor.sendMessage(ChatColor.RED + "Not a faction!");
        return false;
    }

    private boolean rankCreate(CommandInfo info) {
        PlayerGroup group = factionals.getGroupByName(info.args[1]);
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();

        if(group instanceof Faction) {
            if(!group.hasPermission(GroupPermission.RANK_EDIT, factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
                info.executor.sendMessage(ChatColor.RED + "No permission! Contact your faction owner if this is a mistake");
                return false;
            }
            ((Faction) group).unclaim(new PlotLocation(chunk));
            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        }

        info.executor.sendMessage(ChatColor.RED + "Not a faction!");
        return false;
    }
}
