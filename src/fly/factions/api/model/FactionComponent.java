package fly.factions.api.model;

import fly.factions.api.permissions.FactionPermission;

import java.util.Collection;

public interface FactionComponent extends EconomyMember, Permissible, PlayerGroup {
    User getLeader();
    void setLeader(User user);

    boolean hasPermission(User user, FactionPermission permission);

    /**
     * Adds a user to the component's member list
     *
     * @param user the user to add
     */

    void addMember(User user);

    /**
     * Removes a user from a faction's member list
     *
     * @param user the user to remove
     */

    void removeMember(User user);
}
