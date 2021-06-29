package fly.factions.impl.model;

import fly.factions.api.model.LandAdministrator;
import fly.factions.api.model.User;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractLandAdministrator<T> extends AbstractFactionComponent implements LandAdministrator<T> {
    protected Color fillColor = Color.fromRGB(255,255,255);
    protected double fillOpacity = 0.3;

    protected Color borderColor = Color.fromRGB(255,255,255);

    protected List<T> plots = new ArrayList<>();

    protected AbstractLandAdministrator(String name, User leader) {
        super(name, leader);
    }

    @Override
    public Color getFillColor() {
        return fillColor;
    }

    @Override
    public void setFillColor(Color color) {
        this.fillColor = color;
    }

    @Override
    public double getFillOpacity() {
        return fillOpacity;
    }

    @Override
    public void setFillOpacity(double d) {
        this.fillOpacity = d;
    }

    @Override
    public Color getBorderColor() {
        return borderColor;
    }

    @Override
    public void setBorderColor(Color color) {
        this.borderColor = color;
    }


    @Override
    public Collection<T> getPlots() {
        return new ArrayList<>(plots);
    }

    @Override
    public void addPlot(T plot) {
        plots.add(plot);
    }

    @Override
    public void removePlot(T plot) {
        plots.remove(plot);
    }
}
