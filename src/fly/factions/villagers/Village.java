package fly.factions.villagers;

import fly.factions.model.Faction;
import fly.factions.model.Plot;
import fly.factions.villagers.structures.Structure;

import java.util.ArrayList;
import java.util.List;

public class Village {
    private List<Plot> plots = new ArrayList<>();
    private List<Structure> structures = new ArrayList<>();
    private List<VillagerInfo> residents = new ArrayList<>();

    private Market market;
    private Faction faction;

    public Market getMarket() {
        return market;
    }

    public Faction getFaction() {
        return faction;
    }

    public List<Plot> getPlots() {
        return new ArrayList<>(plots);
    }

    public void addPlot(Plot plot) {
        plots.add(plot);
    }

    public void removePlot(Plot plot) {
        plots.remove(plot);
    }

    public boolean hasPlot(Plot plot) {
        return plots.contains(plot);
    }

    public void addStructure(Structure structure) {
        structures.add(structure);
    }

    public List<Structure> getStructures() {
        return new ArrayList<>(structures);
    }

    public void clearStructures() {
        structures = new ArrayList<>();
    }
}
