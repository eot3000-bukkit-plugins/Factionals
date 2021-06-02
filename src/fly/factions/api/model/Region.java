package fly.factions.api.model;

import java.util.Map;

public interface Region extends LandAdministrator {
    Map<Integer, Lot> getLots();

    void setLot(int lotNumber, Lot lot);
}
