package fly.factions.api.model;

import java.util.Collection;
import java.util.Map;

public interface Region extends LandAdministrator<Plot> {
    Map<Integer, Lot> getLots();

    void setLot(int lotNumber, Lot lot);

    Collection<Town> getTowns();

    Town getTown(String name);

    void addTown(Town town);

    void removeTown(Town town);
}
