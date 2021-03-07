package fly.factions.impl.model;

import fly.factions.Factionals;
import fly.factions.api.model.Faction;
import fly.factions.api.model.Plot;
import fly.factions.impl.util.Plots;
import org.bukkit.World;

public class PlotImpl implements Plot {
    private final int x;
    private final int z;
    private final World w;

    private Faction faction;

    public PlotImpl(int x, int z, World w, Faction faction) {
        this.x = x;
        this.z = z;
        this.w = w;

        this.faction = faction;
    }

    @Override
    public void setFaction(Faction faction) {
        Factionals.getFactionals().getRegistry(Plot.class, Integer.class).set(getLocationId(), this);

        this.faction.removePlot(this);

        this.faction = faction;

        this.faction.addPlot(this);
    }

    @Override
    public int getLocationId() {
        return Plots.getLocationId(x, z, w);
    }

    @Override
    public Faction getFaction() {
        return faction;
    }
}
