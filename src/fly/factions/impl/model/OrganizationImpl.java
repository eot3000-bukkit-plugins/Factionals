package fly.factions.impl.model;

import fly.factions.api.exceptions.NotAMemberException;
import fly.factions.api.model.ExecutiveDivision;
import fly.factions.api.model.Organization;
import fly.factions.api.model.Region;
import fly.factions.api.model.User;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class OrganizationImpl extends AbstractFactionComponent implements Organization {
    public OrganizationImpl(String name, User leader) {
        super(name, leader);
    }

    @Override
    public void setLeader(User leader) {
        if(!members.contains(leader)) {
            throw new NotAMemberException();
        }

        this.leader = leader;
    }

    @Override
    public void removeMember(User user) {
        if(user.equals(leader)) {

            return;
        }

        members.remove(user);
    }

    @Override
    public String getId() {
        return "organization-" + name;
    }

    @Override
    public boolean userHasPlotPermissions(User user, boolean owner, boolean pub) {
        return members.contains(user);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.AIR);
    }
}
