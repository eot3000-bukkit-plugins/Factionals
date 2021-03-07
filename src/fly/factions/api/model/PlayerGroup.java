package fly.factions.api.model;

import java.util.Collection;
import java.util.Set;

public interface PlayerGroup {
    void broadcast(String s);

    Collection<User> getMembers();
}
