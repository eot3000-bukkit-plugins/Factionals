package fly.factions.model;

public class Faction extends PlayerGroup {

    public Faction(String name, User leader) {
        super(name, leader);
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
