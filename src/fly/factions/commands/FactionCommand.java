package fly.factions.commands;

import fly.factions.model.*;
import fly.factions.permissions.GroupPermission;
import fly.factions.permissions.GroupRank;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionCommand extends FactionalsCommandExecutor {
    public FactionCommand() {
        registerSubCommand(2, this::createFaction, "create");
        registerSubCommand(4, this::grantCitizen, "citizenship", "grant");
        registerSubCommand(4, this::revokeCitizen, "citizenship", "revoke");
        registerSubCommand(2, this::claim, "claim");
        registerSubCommand(1, this::unclaim, "unclaim");
        registerSubCommand(5, this::rankCreate, "rank", "create");
        registerSubCommand(5, this::rankPermission, "rank", "permission");
        registerSubCommand(5, this::rankPlayerAdd, "rank", "add");
        registerSubCommand(1, this::factionMap, "map");
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
        PlayerGroup group = factionals.getGroupByName(info.args[2]);
        String name = info.args[3];
        User leader = factionals.getUserByName(info.args[4]);

        if(leader == null) {
            info.executor.sendMessage(ChatColor.RED + "No such player!");
            return false;
        }

        if(!group.getMembers().contains(leader)) {
            info.executor.sendMessage(ChatColor.RED + "Leader not member of faction!");
            return false;
        }

        if(group instanceof Faction) {
            if(!group.hasPermission(GroupPermission.RANK_EDIT, factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
                info.executor.sendMessage(ChatColor.RED + "No permission! Contact your faction owner if this is a mistake");
                return false;
            }
            if(group.getRank(name) != null) {
                info.executor.sendMessage(ChatColor.RED + "Rank already exists with that name!");
                return false;
            }

            new GroupRank(group, name, leader);

            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        }

        info.executor.sendMessage(ChatColor.RED + "Not a faction!");
        return false;
    }

    private boolean rankPermission(CommandInfo info) {
        PlayerGroup group = factionals.getGroupByName(info.args[2]);
        GroupPermission permission = null;
        String name = info.args[3];

        try {
            permission = GroupPermission.valueOf(info.args[4].toUpperCase().replaceAll("-", "_"));
        } catch (Exception e) {
            for(GroupPermission groupPermission : GroupPermission.values()) {
                if (groupPermission.faction.equalsIgnoreCase(info.args[4])) {
                    permission = groupPermission;
                    break;
                }
            }
            if(permission == null) {
                info.executor.sendMessage(ChatColor.RED + "No such permission!");
                return false;
            }
        }

        if(group instanceof Faction) {
            if(!group.hasPermission(GroupPermission.RANK_EDIT, factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
                info.executor.sendMessage(ChatColor.RED + "No permission! Contact your faction owner if this is a mistake");
                return false;
            }
            if(group.getRank(name) == null) {
                info.executor.sendMessage(ChatColor.RED + "No such rank!");
                return false;
            }

            group.getRank(name).addPermission(permission);

            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        }

        info.executor.sendMessage(ChatColor.RED + "Not a faction!");
        return false;
    }

    private boolean rankPlayerAdd(CommandInfo info) {
        PlayerGroup group = factionals.getGroupByName(info.args[2]);
        String name = info.args[3];
        User user = factionals.getUserByName(info.args[4]);

        if(user == null) {
            info.executor.sendMessage(ChatColor.RED + "No such player!");
            return false;
        }

        if(group instanceof Faction) {
            if(group.getRank(name) == null) {
                info.executor.sendMessage(ChatColor.RED + "No such rank!");
                return false;
            }
            if(!group.getRank(name).isOwner(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
                info.executor.sendMessage(ChatColor.RED + "No permission! Contact your faction owner if this is a mistake");
                return false;
            }
            group.getRank(name).addMember(user);

            info.executor.sendMessage(ChatColor.GREEN + "Success!");
            return true;
        }

        info.executor.sendMessage(ChatColor.RED + "Not a faction!");
        return false;
    }

    private boolean factionMap(CommandInfo info) {
        List<String> map = new ArrayList<>();
        Map<Faction, String> symbols = new HashMap<>();
        Chunk chunk = ((Player) info.executor).getLocation().getChunk();

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        World world = chunk.getWorld();

        String first = ChatColor.DARK_GRAY.toString();

        for(int i = -10; i < 11; i++) {
            first+="_";
        }

        map.add(first);

        for(int x = -10; x < 11; x++) {
            String current = "";

            for(int z = -10; z < 11; z++) {
                Plot plot = factionals.getPlotByLocation(new PlotLocation(chunkX+x, chunkZ+z, world));

                if(x == z && x == 0) {
                    current+=ChatColor.BLACK + "X";
                    continue;
                }

                if(plot == null) {
                    current+=ChatColor.DARK_GREEN + "-";
                    continue;
                }

                while(!symbols.containsKey(plot.getFaction())) {
                    String rand = randomSymbol();

                    if(!symbols.containsValue(rand)) {
                        symbols.put(plot.getFaction(), rand);
                    }
                }

                current+=symbols.get(plot.getFaction());
            }

            map.add(current);
        }
        for(String line : map) {
            info.executor.sendMessage(line);
        }

        return true;
    }

    private String randomSymbol() {
        List<ChatColor> colors = new ArrayList<>();
        for(ChatColor color : ChatColor.values()) {
            colors.add(color);
        }
        List<Character> characters = Arrays.asList('#', '+', '$', '%', '^', '&', '*', '/', '\\', '=');

        colors.remove(ChatColor.DARK_GREEN);
        colors.remove(ChatColor.BLACK);

        return colors.get(random.nextInt(colors.size())).toString() + characters.get(random.nextInt(characters.size()));
    }
}
