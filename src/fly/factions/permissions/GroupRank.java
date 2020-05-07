package fly.factions.permissions;

import fly.factions.Factionals;
import fly.factions.model.PlayerGroup;
import fly.factions.model.PlotOwner;
import fly.factions.model.Savable;
import fly.factions.model.User;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class GroupRank extends PlotOwner implements Savable {
    protected final PlayerGroup group;
    protected String name;
    protected User leader;
    protected List<GroupPermission> permissions = new ArrayList<>();
    protected List<User> members = new ArrayList<>();

    public GroupRank(PlayerGroup group, String name, User leader) {
        this.group = group;
        this.name = name;
        this.leader = leader;
        members.add(leader);

        group.addRank(this);
    }

    public static void createRank(ConfigurationSection section, PlayerGroup group) {
        User leader = Factionals.getFactionals().getUserByUUID(UUID.fromString(section.getString("leader")));
        GroupRank rank = new GroupRank(group, section.getString("name"), leader);

        for(String member : section.getStringList("members")) {
            rank.addMember(Factionals.getFactionals().getUserByUUID(UUID.fromString(member)));
        }

        for(String member : section.getStringList("permissions")) {
            rank.addMember(Factionals.getFactionals().getUserByUUID(UUID.fromString(member)));
        }
    }

    private GroupPermission getPermission(String string) {
        return GroupPermission.valueOf(string);
    }

    public void addPermission(GroupPermission permission) {
        permissions.add(permission);
    }

    public void removePermission(GroupPermission permission) {
        permissions.remove(permission);
    }

    public boolean hasPermission(GroupPermission permission) {
        return permissions.contains(permission);
    }

    public String getName() {
        return name;
    }

    public List<User> getMembers() {
        return new ArrayList<>(members);
    }

    public void addMember(User user) {
        members.add(user);
    }

    public void removeMember(User user) {
        members.remove(user);
    }

    @Override
    public Map<String, Object> saveInfo() {
        Map<String, Object> ret = new HashMap<>();
        List<String> membersString = new ArrayList<>();

        for(User user : getMembers()) {
            membersString.add(user.getUuid().toString());
        }

        ret.put("members", membersString);
        ret.put("name", name);
        ret.put("leader", leader.getUuid().toString());
        ret.put("permissions", permissions);
        return ret;
    }

    @Override
    public boolean isOwner(User user) {
        return group.getLeader().equals(user) || leader.equals(user);
    }

    @Override
    public boolean canDo(User user) {
        return members.contains(user);
    }

    @Override
    public int id() {
        return 2;
    }

    @Override
    public String uniqueId() {
        return group.getName() + "_" + name;
    }

    @Override
    public String niceName() {
        return group.niceName();
    }

    @Override
    public void addMoney(double d) {
        group.addMoney(d);
    }

    @Override
    public void removeMoney(double d) {
        group.removeMoney(d);
    }

    @Override
    public void setMoney(double d) {
        group.setMoney(d);
    }

    @Override
    public double getMoney() {
        return group.getMoney();
    }
}
