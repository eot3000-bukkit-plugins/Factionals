package fly.factions.api.model;

import org.bukkit.Color;

import java.util.Collection;

public interface LandAdministrator extends FactionComponent {
    Collection<Plot> getPlots();

    void addPlot(Plot plot);
    void removePlot(Plot plot);

    String getDesc();

    Color getFillColor();
    void setFillColor(Color color);

    double getFillOpacity();
    void setFillOpacity(double d);

    Color getBorderColor();
    void setBorderColor(Color color);

    double getBorderOpacity();
}
