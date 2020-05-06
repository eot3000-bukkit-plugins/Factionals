package fly.factions.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends PlotOwner {
    private UUID uuid;
    private List<PlayerGroup> memberOf = new ArrayList<>();

    public User(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void joinOrganization(PlayerGroup group) {
        memberOf.add(group);
    }

    public void leaveOrganization(PlayerGroup group) {
        memberOf.remove(group);
    }

    @Override
    public boolean doesOwnPlots(User user) {
        return this.equals(user);
    }

    @Override
    public boolean canDo(User user) {
        return doesOwnPlots(user);
    }

    @Override
    public int id() {
        return 0;
    }

    @Override
    public String uniqueId() {
        return getUuid().toString();
    }

    @Override
    public void addMoney(double d) {

    }

    @Override
    public void removeMoney(double d) {

    }

    @Override
    public void setMoney(double d) {

    }

    @Override
    public double getMoney() {
        return 0;
    }
}
