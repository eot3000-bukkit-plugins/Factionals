package fly.factions.api.model;

import java.util.Collection;

public interface LandAdministrator extends FactionComponent {
    Collection<Plot> getPlots();

    void addPlot(Plot plot);
    void removePlot(Plot plot);
}
