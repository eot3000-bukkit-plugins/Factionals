package fly.factions.model;

import fly.factions.messages.Messages;

import java.util.HashSet;
import java.util.Set;

public abstract class PlayerGroup implements EconomyMember {
    protected long creationDate;
    protected boolean deleted;

    protected String name;
    protected EconomyMember leader;
    protected Set<User> members = new HashSet<>();

    public PlayerGroup(String name, EconomyMember leader) {
        this.name = name;
        this.leader = leader;

        this.creationDate = System.currentTimeMillis();
    }

    public PlayerGroup(String name, EconomyMember leader, long millis) {
        this.name = name;
        this.leader = leader;

        this.creationDate = millis;
    }

    public String getName() {
        return name;
    }

    public EconomyMember getLeader() {
        return leader;
    }

    public Set<User> getMembers() {
        return new HashSet<>(members);
    }

    public void setLeader(EconomyMember leader) {
        this.leader = leader;
    }

    public void addMember(User user) {
        members.add(user);
    }

    public void removeMember(User user) {
        members.remove(user);

        if(members.size() == 0) {
            factionals.deleteFaction((Faction) this);
            this.deleted = true;
        }

        if(this instanceof Faction) {
            ((Faction) this).tellEveryone(Messages.FACTION_MESSAGE_MEMBER_LEAVE.replaceAll("%1", user.getName()));
        }
    }

    public long getCreationDate() {
        return creationDate;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
