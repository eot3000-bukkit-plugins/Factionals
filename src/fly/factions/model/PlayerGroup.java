package fly.factions.model;

import java.util.HashSet;
import java.util.Set;

public abstract class PlayerGroup implements EconomyMember {
    protected String name;
    protected EconomyMember leader;
    protected Set<User> members = new HashSet<>();

    public PlayerGroup(String name, EconomyMember leader) {
        this.name = name;
        this.leader = leader;
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
}
