package fly.factions.villagers.structures;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;

import java.util.HashSet;
import java.util.Set;

public abstract class Structure {
    private Location door;
    private Set<Location> blocks;
    private BlockFace face;
    private StructureType type;

    Structure(Location door, Set<Location> blocks, BlockFace face, StructureType type) {
        this.door = door;
        this.blocks = blocks;
        this.face = face;
        this.type = type;
    }

    public Location getDoor() {
        return door;
    }

    public Location walkLocation() {
        return door.clone().add(face.getModX(), 0, face.getModZ());
    }

    public Set<Location> getBlocks() {
        return new HashSet<>(blocks);
    }

    public StructureType getType() {
        return type;
    }

    public enum StructureType {
        HOUSING("House") {
            @Override
            public boolean validSpecial(Block block) {
                return block.getState().getBlockData() instanceof Bed;
            }

            @Override
            public Structure createNew(Location door, Set<Location> scanned, BlockFace face) {
                HousingStructure structure = new HousingStructure(door, scanned, face);

                for(Location location : scanned) {
                    BlockData data = location.getBlock().getState().getBlockData();

                    if(data instanceof Bed) {
                        if(((Bed) data).getPart().equals(Bed.Part.HEAD)) {
                            structure.addBed(location);
                        }
                    }
                }
                return structure;
            }
        };

        private String name;

        StructureType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public abstract boolean validSpecial(Block block);

        public abstract Structure createNew(Location door, Set<Location> scanned, BlockFace face);
    }
}
