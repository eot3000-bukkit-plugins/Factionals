package fly.factions.model;

import java.util.HashMap;
import java.util.Map;

public class Faction extends PlayerGroup {
    private Map<PlotLocation, Plot> claimed = new HashMap<>();
    private int taxes;

    public Faction(User leader, String name) {
        super(leader, name);
    }

    public void claim(Plot plot) {
        claimed.put(plot.getLocation(), plot);
    }

    public void unclaim(PlotLocation plot) {
        claimed.remove(plot);
    }

    @Override
    public String niceName() {
        return "Faction " + name;
    }
}
