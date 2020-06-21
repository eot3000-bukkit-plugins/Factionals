package fly.factions.model;

import fly.factions.villagers.Village;

import java.util.*;

public class Faction extends PlayerGroup {
    private Map<PlotLocation, Plot> claimed = new HashMap<>();
    private Set<Village> villages = new HashSet<>();
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

    public void addVillage(Village village) {
        villages.add(village);
    }

    public void removeVillage(Village village) {
        villages.remove(village);
    }

    public Set<Village> getVillages() {
        return new HashSet<>(villages);
    }

    public List<Plot> getClaimedPlots() {
        return new ArrayList<>(claimed.values());
    }

    @Override
    public String niceName() {
        return "Faction " + name;
    }
}
