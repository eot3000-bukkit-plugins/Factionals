package fly.factions.villagers.structures;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.Set;

public class HousingStructure extends Structure {
    private Set<Location> beds = new HashSet<>();

    public HousingStructure(Location door, Set<Location> blocks, BlockFace face) {
        super(door, blocks, face, StructureType.HOUSING);
    }

    public Set<Location> getBeds() {
        return new HashSet<>(beds);
    }

    public void addBed(Location location) {
        beds.add(location);
    }
}
