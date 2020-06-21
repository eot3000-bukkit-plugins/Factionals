package fly.factions.utils;

import fly.factions.model.Plot;
import fly.factions.villagers.Village;

public class TownUtils {
    public static Village getVillage(Plot plot) {
        if(plot == null) {
            return null;
        }

        for(Village village : plot.getFaction().getVillages()) {
            for(Plot villagePlot : village.getPlots()) {
                if(villagePlot.equals(plot)) {
                    return village;
                }
            }
        }

        return null;
    }
}
