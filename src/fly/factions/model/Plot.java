package fly.factions.model;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

//PERMS:
//0- Owner
//1- Build
//2- Containers
//3- Doors and trapdoors
//4- Levers and buttons
//5- Armor stands, lecterns, item frames
//6- Slimefun
//7- Boats, redstone, minecarts, any other interactions

public class Plot {

    private Plot() {

    }

    public static Integer getLocationId(Location location) {
        return getLocationId(location.getChunk());
    }

    public static Integer getLocationId(Chunk chunk) {
        return getLocationId(chunk.getX(), chunk.getZ(), chunk.getWorld());
    }

    public static Integer getLocationId(int x, int z, World world) {
        return ((x+2048) | ((z+2048) << 12)) | (world.getEnvironment().getId() << 24);
    }


}
