package fly.factions.utils;

import fly.factions.Factionals;
import fly.factions.model.Plot;
import fly.factions.model.PlotLocation;
import fly.factions.villagers.Village;
import fly.factions.villagers.structures.Structure;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class HouseUtils {
    private static List<BlockFace> VALID_FACES = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    private static Factionals factionals = Factionals.getFactionals();

    public static void startFinding(Location location) {
        org.bukkit.material.Sign sign = (org.bukkit.material.Sign) location.getBlock().getState().getData();
        Sign state = ((Sign) location.getBlock().getState());

        if(!sign.isWallSign()) {
            state.setLine(0, "");
            state.setLine(1, ChatColor.DARK_RED + "INVALID!");
            state.setLine(2, ChatColor.RED + "Sign not on wall!");
            state.setLine(3, "");
            state.update();
            return;
        }

        BlockFace face = sign.getFacing();

        Location newPosBase = location.clone().add(-face.getModX(), 0, -face.getModZ());

        List<Location> possibleDoors = new ArrayList<>();

        possibleDoors.add(newPosBase.clone().add(-face.getModZ(), 0, -face.getModX()));
        possibleDoors.add(newPosBase.clone().add(face.getModZ(), 0, face.getModX()));
        possibleDoors.add(newPosBase.clone().add(0, -1, 0));

        boolean doorFound = false;
        Location door = null;

        for(Location doorLoc : possibleDoors) {
            BlockData data = doorLoc.getBlock().getState().getBlockData();

            if(data instanceof Door) {
                if(((Door) data).getHalf().equals(Bisected.Half.TOP)) {
                    if(!doorFound) {
                        doorFound = true;
                        door = doorLoc;
                        continue;
                    }

                    state.setLine(0, "");
                    state.setLine(1, ChatColor.DARK_RED + "INVALID!");
                    state.setLine(2, ChatColor.RED + "More than 1 door!");
                    state.setLine(3, "");
                    state.update();
                    return;
                }
            }
        }

        if(!doorFound) {
            state.setLine(0, "");
            state.setLine(1, ChatColor.DARK_RED + "INVALID!");
            state.setLine(2, ChatColor.RED + "No door found!");
            state.setLine(3, "");
            state.update();
            return;
        }

        Structure.StructureType type = null;

        for(Structure.StructureType structureType : Structure.StructureType.values()) {
            if(state.getLine(0).equals(structureType.getName())) {
                type = structureType;
                break;
            }
        }

        if(type == null) {
            state.setLine(0, "");
            state.setLine(1, ChatColor.DARK_RED + "INVALID!");
            state.setLine(2, ChatColor.RED + "Invalid structure");
            state.setLine(3, ChatColor.RED + "type!");
            state.update();
            return;
        }

        Plot plot = factionals.getPlotByLocation(new PlotLocation(door));
        Village village = TownUtils.getVillage(plot);

        if(village == null) {
            state.setLine(0, "");
            state.setLine(1, ChatColor.DARK_RED + "INVALID!");
            state.setLine(2, ChatColor.RED + "No village!");
            state.setLine(3, ChatColor.RED + "");
            state.update();
            return;
        }

        Set<Location> scanned = validHouse(door, sign.getFacing(), type, village);

        if(scanned == null) {
            state.setLine(0, "");
            state.setLine(1, ChatColor.DARK_RED + "INVALID!");
            state.setLine(2, ChatColor.RED + "Invalid building!");
            state.setLine(3, "");
            state.update();
            return;
        }

        Structure structure = type.createNew(door, scanned, face);
        village.addStructure(structure);
    }

    public static Set<Location> validHouse(Location door, BlockFace facing, Structure.StructureType type, Village village) {
        Set<Location> locations = new HashSet<>();

        Location newPos = door.clone().add(-facing.getModX(), 0, -facing.getModZ());

        return scanSubChunk(newPos, locations, type, village) ? locations : null;
    }

    private static boolean scanSubChunk(Location location, Set<Location> locations, Structure.StructureType type, Village village) {
        if(locations.size() > 300) {
            return false;
        }

        if(location.getBlock().getType().equals(Material.AIR) || type.validSpecial(location.getBlock())) {
            if(hasRoof(location)) {
                if(!village.hasPlot(factionals.getPlotByLocation(new PlotLocation(location)))) {
                    return false;
                }
                if (locations.contains(location)) {
                    return true;
                }

                locations.add(location);

                for (BlockFace face : VALID_FACES) {
                    if (!scanSubChunk(location.clone().add(face.getModX(), 0, face.getModZ()), locations, type, village)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static boolean hasRoof(Location location) {
        Location newPos = location.clone().add(0, 1, 0);

        for(int x = 0; x < 10; x++) {
                if(!newPos.add(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
                return true;
            }
        }

        return false;
    }
}
