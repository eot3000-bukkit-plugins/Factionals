package fly.factions.model;

import fly.factions.Factionals;
import fly.factions.permissions.GroupPermission;
import fly.factions.permissions.GroupRank;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class PlayerGroup extends PlotOwner implements Savable {
    protected User leader;
    protected Set<User> members = new HashSet<>();

    protected Map<String, GroupRank> ranks = new HashMap<>();
    protected String name;

    protected int money;

    public PlayerGroup(User leader, String name) {
        this.leader = leader;
        this.name = name;

        members.add(leader);
    }

    public void addMember(User user) {
        members.add(user);
        user.joinOrganization(this);
    }

    public void removeMember(User user) {
        members.remove(user);
        user.leaveOrganization(this);
    }

    public User getLeader() {
        return leader;
    }

    public List<User> getMembers() {
        return new ArrayList<>(members);
    }

    public void addRank(GroupRank rank) {
        ranks.put(rank.getName(), rank);
    }

    public GroupRank getRank(String name) {
        return ranks.get(name);
    }

    public List<GroupRank> getRanks(User user) {
        List<GroupRank> ret = new ArrayList<>();

        for(GroupRank rank : ranks.values()) {
            if(rank.getMembers().contains(user)) {
                ret.add(rank);
            }
        }

        return ret;
    }

    public Map<String, GroupRank> getRanks() {
        return new HashMap<>(ranks);
    }

    public boolean hasPermission(GroupPermission permission, User user) {
        for(GroupRank rank : getRanks(user)) {
            if(rank.hasPermission(permission))  {
                return true;
            }
        }

        return false;
    }

    public String getName() {
        return name;
    }

    public static void createNew(YamlConfiguration configuration) {
        PlayerGroup group;
        ConfigurationSection section = configuration.getConfigurationSection("group");

        User leader = Factionals.getFactionals().getUserByUUID(UUID.fromString(section.getString("leader")));
        String name = section.getString("name");

        if(section.getBoolean("is-faction")) {
            group = new Faction(leader, name);
        } else {
            group = new PlayerGroup(leader, name);
        }

        ConfigurationSection ranks = section.getConfigurationSection("ranks");
        for(String string : ranks.getKeys(false)) {
            GroupRank.createRank(ranks.getConfigurationSection(string), group);
        }

        Factionals.getFactionals().addGroup(group);
    }

    @Override
    public boolean doesOwnPlots(User user) {
        return leader.getUuid().equals(user.getUuid());
    }

    @Override
    public boolean canDo(User user) {
        return members.contains(user);
    }

    @Override
    public Map<String, Object> saveInfo() {
        Map<String, Object> ret = new HashMap<>();
        List<String> membersString = new ArrayList<>();
        Map<String, Object> ranksList = new HashMap<>();

        for(User user : getMembers()) {
            membersString.add(user.getUuid().toString());
        }
        for(GroupRank rank : ranks.values()) {
            ranksList.put(rank.getName(), rank.saveInfo());
        }

        ret.put("leader", leader.getUuid().toString());
        ret.put("name", name);
        ret.put("is-faction", this instanceof Faction);
        ret.put("members", membersString);
        ret.put("ranks", ranksList);

        return ret;
    }

    @Override
    public int id() {
        return 1;
    }

    @Override
    public String uniqueId() {
        return name.toLowerCase();
    }

    @Override
    public double getMoney() {
        return money;
    }

    @Override
    public void setMoney(double d) {
        this.money = (int) Math.floor(d);
    }

    @Override
    public void addMoney(double d) {
        this.money+=(int) Math.floor(d);
    }

    @Override
    public void removeMoney(double d) {
        this.money-=(int) Math.floor(d);
    }
}
