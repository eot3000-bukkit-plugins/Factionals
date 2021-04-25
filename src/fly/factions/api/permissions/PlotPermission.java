package fly.factions.api.permissions;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;

public enum PlotPermission {
    //Block placing and breaking
    BUILD {
        @Override
        public boolean required(Block block, Action action, boolean shift) {
            return false;
        }

        @Override
        public boolean required(Entity entity) {
            return false;
        }
    },

    //Doors and trapdoors
    DOOR {
        @Override
        public boolean required(Block block, Action action, boolean shift) {
            BlockData data = block.getState().getBlockData();

            return data instanceof TrapDoor || data instanceof Door || data instanceof Gate;
        }

        @Override
        public boolean required(Entity entity) {
            return false;
        }
    },

    //Levers and buttons
    SWITCH {
        @Override
        public boolean required(Block block, Action action, boolean shift) {
            return block.getState().getBlockData() instanceof Switch;
        }

        @Override
        public boolean required(Entity entity) {
            return false;
        }
    },

    //Chests etc
    CONTAINER {
        @Override
        public boolean required(Block block, Action action, boolean shift) {
            return false;
        }

        @Override
        public boolean required(Entity entity) {
            return entity instanceof Minecart && ((Minecart) entity).getDisplayBlock() instanceof Container;
        }
    },

    //Comparators, repeaters and daylight detectors
    REDSTONE {
        @Override
        public boolean required(Block block, Action action, boolean shift) {
            BlockData data = block.getState().getBlockData();

            return data instanceof Repeater || data instanceof Comparator || data instanceof DaylightDetector;
        }

        @Override
        public boolean required(Entity entity) {
            return false;
        }
    },

    //Boats and minecarts
    VEHICLES {
        @Override
        public boolean required(Block block, Action action, boolean shift) {
            return false;
        }

        @Override
        public boolean required(Entity entity) {
            return false;
        }
    },

    //Plants, composters and beehives
    FARMING {
        @Override
        public boolean required(Block block, Action action, boolean shift) {
            BlockData data = block.getState().getBlockData();

            return data instanceof Sapling || block.getType().equals(Material.COMPOSTER) || data instanceof Beehive;
        }

        @Override
        public boolean required(Entity entity) {
            return false;
        }
    },

    //Item frames, armor stands, paintings, jukeboxes, note blocks, flower pots and lecterns
    DETAILS {
        @Override
        public boolean required(Block block, Action action, boolean shift) {
            BlockData data = block.getState().getBlockData();

            return data instanceof Jukebox || Tag.FLOWER_POTS.isTagged(block.getType()) || data instanceof NoteBlock;
        }

        @Override
        public boolean required(Entity entity) {
            return entity instanceof ArmorStand || entity instanceof ItemFrame || entity instanceof Painting;
        }
    },

    //Pressure plates, trip wires, and crop trampling
    PRESSURE_PLATE {
        @Override
        public boolean required(Block block, Action action, boolean shift) {
            return action.equals(Action.PHYSICAL);
        }

        @Override
        public boolean required(Entity entity) {
            return false;
        }
    };

    PlotPermission() {

    }

    public abstract boolean required(Block block, Action action, boolean shift);

    public abstract boolean required(Entity entity);
}
