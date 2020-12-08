package fly.factions.model;

public class Region extends PlayerGroup {
    private Faction faction;

    public Region(String name, User leader, Faction faction) {
        super(name, leader);

        this.faction = faction;
    }

    public Faction getFaction() {
        return faction;
    }

    @Override
    public double getMoney() {
        return 0;
    }

    @Override
    public void setMoney(double x) {

    }

    @Override
    public void addMoney(double x) {

    }

    @Override
    public void takeMoney(double x) {

    }
}
