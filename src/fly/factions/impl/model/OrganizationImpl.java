package fly.factions.impl.model;

import fly.factions.api.model.Organization;
import fly.factions.api.model.User;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class OrganizationImpl implements Organization {
    @Override
    public double getBalance() {
        return 0;
    }

    @Override
    public void setBalance(double x) {

    }

    @Override
    public void addToBalance(double x) {

    }

    @Override
    public void takeFromBalance(double x) {

    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean userHasPlotPermissions(User user, boolean owner, boolean pub) {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return null;
    }

    @Override
    public void broadcast(String s) {

    }

    @Override
    public Collection<User> getMembers() {
        return null;
    }
}
