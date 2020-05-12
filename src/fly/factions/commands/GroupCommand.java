package fly.factions.commands;

import fly.factions.model.PlayerGroup;
import fly.factions.model.User;
import fly.factions.permissions.GroupPermission;
import fly.factions.permissions.GroupRank;
import org.bukkit.entity.Player;

public class GroupCommand extends FactionalsCommandExecutor {
    boolean faction;

    public GroupCommand() {
        this(false);

        registerSubCommand(2, this::createGroup, "create")
                .also(2, nameTaken, pGroupNull);

        registerSubCommand(4, this::addMember, "member", "add")
                .also(3, notAUser, pPlayerNotNull)
                .also(4, notAGroup, pGroupNotNull)
                .groupPermission(4, GroupPermission.MEMBER_INVITE);

        registerSubCommand(4, this::removeMember, "member", "remove")
                .also(3, notAUser, pPlayerNotNull)
                .also(4, notAGroup, pGroupNotNull)
                .groupPermission(4, GroupPermission.MEMBER_KICK);

        registerSubCommand(5, this::rankCreate, "rank", "create")
                .also(3, notAGroup, pGroupNotNull)
                .also(5, notAUser, pPlayerNotNull)
                .groupPermission(3, GroupPermission.RANK_EDIT);

        registerSubCommand(6, this::rankPermissionAdd, "rank", "permission", "add")
                .also(4, notAGroup, pGroupNotNull)
                .also(6, notAPermission, pGroupPermission)
                .groupPermission(4, GroupPermission.RANK_EDIT);

        registerSubCommand(6, this::rankPermissionRemove, "rank", "permission", "remove")
                .also(4, notAGroup, pGroupNotNull)
                .also(6, notAPermission, pGroupPermission)
                .groupPermission(4, GroupPermission.RANK_EDIT);

        registerSubCommand(6, this::rankPlayerAdd, "rank", "player", "add")
                .also(4, notAGroup, pGroupNotNull)
                .also(6, notAUser, pPlayerNotNull);

        registerSubCommand(6, this::rankPlayerRemove, "rank", "player", "remove")
                .also(4, notAGroup, pGroupNotNull)
                .also(6, notAUser, pPlayerNotNull);
    }

    public GroupCommand(boolean faction) {
        this.faction = faction;
    }

    protected boolean createGroup(CommandInfo info) {
        factionals.addGroup(new PlayerGroup(factionals.getUserByUUID(((Player)info.executor).getUniqueId()), info.args[1]));
        info.executor.sendMessage(success);
        return true;
    }

    protected boolean addMember(CommandInfo info) {
        factionals.getGroupByName(info.args[3]).addMember(factionals.getUserByName(info.args[2]));
        info.executor.sendMessage(success);
        return true;
    }

    protected boolean removeMember(CommandInfo info) {
        factionals.getGroupByName(info.args[3]).removeMember(factionals.getUserByName(info.args[2]));
        info.executor.sendMessage(success);
        return true;
    }

    protected boolean rankCreate(CommandInfo info) {
        PlayerGroup group = factionals.getGroupByName(info.args[3]);
        GroupRank rank = group.getRank(info.args[4]);
        User user = factionals.getUserByName(info.args[5]);

        if(!group.getMembers().contains(user)) {
            info.executor.sendMessage(userNotInFaction);
            return false;
        }
        if(rank != null) {
            info.executor.sendMessage(nameTaken);
            return false;
        }
        new GroupRank(group, info.args[4], user);
        info.executor.sendMessage(success);
        return true;
    }

    protected boolean rankPermissionAdd(CommandInfo info) {
        PlayerGroup group = factionals.getGroupByName(info.args[3]);
        GroupRank rank = group.getRank(info.args[4]);
        GroupPermission permission = getPermission(info.args[5]);

        if(rank == null) {
            info.executor.sendMessage(notARank);
            return false;
        }
        rank.addPermission(permission);
        info.executor.sendMessage(success);
        return true;
    }

    protected boolean rankPermissionRemove(CommandInfo info) {
        PlayerGroup group = factionals.getGroupByName(info.args[3]);
        GroupRank rank = group.getRank(info.args[4]);
        GroupPermission permission = getPermission(info.args[5]);

        if(rank == null) {
            info.executor.sendMessage(notARank);
            return false;
        }
        rank.removePermission(permission);
        info.executor.sendMessage(success);
        return true;
    }

    private GroupPermission getPermission(String s) {
        for(GroupPermission permission : GroupPermission.values()) {
            if(s.equalsIgnoreCase(permission.normal) && !faction) {
                return permission;
            }

            if(s.equalsIgnoreCase(permission.faction) && faction) {
                return permission;
            }
        }

        return null;
    }

    protected boolean rankPlayerAdd(CommandInfo info) {
        PlayerGroup group = factionals.getGroupByName(info.args[3]);
        GroupRank rank = group.getRank(info.args[4]);
        User user = factionals.getUserByName(info.args[5]);

        if(!group.getMembers().contains(user)) {
            info.executor.sendMessage(userNotInFaction);
            return false;
        }
        if(rank == null) {
            info.executor.sendMessage(notARank);
            return false;
        }
        if(!rank.isOwner(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(noPermission);
            return false;
        }
        rank.addMember(user);
        info.executor.sendMessage(success);
        return true;
    }

    protected boolean rankPlayerRemove(CommandInfo info) {
        PlayerGroup group = factionals.getGroupByName(info.args[3]);
        GroupRank rank = group.getRank(info.args[4]);
        User user = factionals.getUserByName(info.args[5]);

        if(!group.getMembers().contains(user)) {
            info.executor.sendMessage(userNotInFaction);
            return false;
        }
        if(rank == null) {
            info.executor.sendMessage(notARank);
            return false;
        }
        if(!rank.isOwner(factionals.getUserByUUID(((Player) info.executor).getUniqueId()))) {
            info.executor.sendMessage(noPermission);
            return false;
        }
        rank.removeMember(user);
        info.executor.sendMessage(success);
        return true;
    }
}
