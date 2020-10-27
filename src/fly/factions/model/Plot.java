package fly.factions.model;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class Plot {
    public static Integer getLocationId(Location location) {
        return getLocationId(location.getChunk());
    }

    public static Integer getLocationId(Chunk chunk) {
        return getLocationId(chunk.getX(), chunk.getZ(), chunk.getWorld());
    }

    public static Integer getLocationId(int x, int z, World world) {
        return ((x << 12) | z) | (world.getEnvironment().getId() << 24);
    }
}
