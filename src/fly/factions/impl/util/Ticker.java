package fly.factions.impl.util;

import fly.factions.Factionals;
import fly.factions.api.model.ExecutiveDivision;
import fly.factions.api.model.Faction;
import fly.factions.api.model.Region;
import fly.factions.api.model.User;
import fly.factions.api.registries.Registry;

import java.util.UUID;

public class Ticker {
    private static Registry<Faction, String> factionRegistry;
    private static Registry<User, UUID> userRegistry;

    static {
        factionRegistry = Factionals.getFactionals().getRegistry(Faction.class, String.class);
        userRegistry = Factionals.getFactionals().getRegistry(User.class, UUID.class);
    }

    public static void tick() {
        for(Faction faction : factionRegistry.list()) {
            if(!faction.isDeleted()) {
                //for()
            }
        }

        for(User user : userRegistry.list()) {
            if(!user.getFaction().getMembers().contains(user) || user.getFaction().isDeleted()) {
                user.setFaction(null);
            }
        }
    }
}
