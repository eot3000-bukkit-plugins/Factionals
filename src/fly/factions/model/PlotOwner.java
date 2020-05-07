package fly.factions.model;

import fly.factions.Factionals;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class PlotOwner {
    private List<Plot> plots = new ArrayList<>();
    protected Economy economy = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();

    public static PlotOwner getPlotOwner(int id, String uniqueId) {
        switch (id) {
            case (0): {
                return Factionals.getFactionals().getUserByUUID(UUID.fromString(uniqueId));
            }
            case (1): {
                return Factionals.getFactionals().getGroupByName(uniqueId.toLowerCase());
            }
            case (2): {
                String[] split = uniqueId.toLowerCase().split("_");
                return Factionals.getFactionals().getGroupByName(split[0]).getRank(split[1]);
            }
        }
        return null;
    }

    public abstract boolean isOwner(User user);

    public abstract boolean canDo(User user);

    public abstract int id();

    public abstract String uniqueId();

    public abstract String niceName();

    public abstract void addMoney(double d);

    public abstract void removeMoney(double d);

    public abstract void setMoney(double d);

    public abstract double getMoney();

    public List<Plot> getOwnedPlots() {
        return new ArrayList<>(plots);
    }

    public void addPlot(Plot plot) {
        plots.add(plot);
    }

    public void removePlot(Plot plot) {
        plots.remove(plot);
    }
}
