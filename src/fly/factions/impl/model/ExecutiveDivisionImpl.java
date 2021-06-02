package fly.factions.impl.model;

import fly.factions.api.model.ExecutiveDivision;
import fly.factions.api.model.Faction;
import fly.factions.api.model.User;
import fly.factions.api.permissions.FactionPermission;
import fly.factions.api.permissions.Permissibles;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;

public class ExecutiveDivisionImpl extends AbstractFactionComponent implements ExecutiveDivision {
    private Faction faction;

    private EnumSet<FactionPermission> permissions = EnumSet.noneOf(FactionPermission.class);

    public ExecutiveDivisionImpl(String name, User leader, Faction faction) {
        super(name, leader);

        this.faction = faction;

        Permissibles.add(faction.getName() + ":" + name, this);
        Permissibles.add(getId(), this);
    }

    @Override
    public String getId() {
        return faction.getId() + "-department-" + name;
    }

    @Override
    public ItemStack getItem() {
        //TODO: Fix
        return new ItemStack(Material.AIR);
    }

    @Override
    public void addPermission(FactionPermission permission) {
        permissions.add(permission);
    }

    @Override
    public void removePermission(FactionPermission permission) {
        permissions.remove(permission);
    }

    @Override
    public boolean canDo(FactionPermission permission) {
        return permissions.contains(permission);
    }

    @Override
    public boolean userHasPlotPermissions(User user, boolean owner, boolean pub) {
        return owner ? leader.equals(user) || faction.getLeader().equals(user) : pub ? faction.getMembers().contains(user) : members.contains(user);
    }

    @Override
    public void removeMember(User user) {
        members.remove(user);

        if(user.equals(leader)) {
            this.leader = faction.getLeader();
        }
    }
}
