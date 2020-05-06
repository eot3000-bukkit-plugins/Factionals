package fly.factions.model;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class PlotLocation {
    public final int x;
    public final int z;
    public final World world;

    private final int hashCode;

    public PlotLocation(Location location) {
        this(location.getChunk());
    }

    public PlotLocation(Chunk chunk) {
        this(chunk.getX(), chunk.getZ(), chunk.getWorld());
    }

    public PlotLocation(int x, int z, World world) {
        this.x = x;
        this.z = z;
        this.world = world;

        this.hashCode = (x | (z >> 12)) | (world.getEnvironment().getId() >> 24);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PlotLocation)) {
            return false;
        }
        return this.hashCode == ((PlotLocation) obj).hashCode;
    }

    @Override
    public String toString() {
        return world.getName() + "," + x + "," + z;
    }
}
