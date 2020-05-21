package fly.factions.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.*;

public class HouseUtils {
    private static List<BlockFace> VALID_FACES = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    public static boolean validHouse(Location door, BlockFace facing) {
        Map<Integer, Set<Location>> locations = new HashMap<>();

        Location newPos = door.clone().add(-facing.getModX(), 0, -facing.getModZ());

        scanSubChunk(newPos, locations, 0);

        if(Math.abs(locations.size()) > 30) {
            return false;
        }

        for(int i : locations.keySet()) {
            if(locations.get(i).size() > 70) {
                return false;
            }
            System.out.println(i);
            System.out.println(locations.get(i).size());
            System.out.println("-------------");
        }

        return true;
    }

    public static void scanSubChunk(Location location, Map<Integer, Set<Location>> locations, int height) {
        locations.computeIfAbsent(height, (x) -> new HashSet<>());

        System.out.println(location.toString());
        System.out.println(height);
        System.err.println("-------------------------");

        if(locations.get(height).size() > 70) {
            return;
        }

        if(Math.abs(height) > 30) {
            return;
        }

        if(location.getBlock().getType().equals(Material.AIR)) {
            if(locations.get(height).contains(location)) {
                return;
            }

            for(BlockFace face : VALID_FACES) {
                scanSubChunk(location.clone().add(face.getModX(), 0, face.getModZ()), locations, height);
            }

            scanSubChunk(location.clone().add(0, 1, 0), locations, height + 1);

            locations.get(height).add(location);
        }
    }
}
