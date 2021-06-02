package fly.factions.api.model;

import javafx.util.Pair;
import org.bukkit.World;

import java.util.List;

public interface Lot extends LandDivision {
    World getWorld();

    List<Pair<Integer, Integer>> getBlocks();

    List<Plot> getChunks();


}
