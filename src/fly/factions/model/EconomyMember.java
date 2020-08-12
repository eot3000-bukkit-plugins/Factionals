package fly.factions.model;

import fly.factions.serialization.Savable;

public interface EconomyMember extends Savable {
    double getMoney();
    void setMoney(double x);

    void addMoney(double x);
    void takeMoney(double x);
}
