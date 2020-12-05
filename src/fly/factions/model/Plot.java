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
    static {
        try {
            getXMask = binaryToInteger("00000000000000000000111111111111");
            getZMask = binaryToInteger("00000000111111111111000000000000");
            getWMask = binaryToInteger("11111111000000000000000000000000");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getXMask;
    public static int getZMask;
    public static int getWMask;

    private Plot() {

    }

    private static int binaryToInteger(String binary) {
        char[] numbers = binary.toCharArray();
        int result = 0;
        for(int i=numbers.length - 1; i>=0; i--)
            if(numbers[i]=='1')
                result += Math.pow(2, (numbers.length-i - 1));
        return result;
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

    public static int getX(int location) {
        return (location & getXMask)-2048;
    }

    public static int getZ(int location) {
        return ((location & getZMask) >> 12)-2048;
    }

    public static int getW(int location) {
        return (location & getWMask) >> 24;
    }
}
