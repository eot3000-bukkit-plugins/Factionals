package fly.factions.villagers.nms;

import net.minecraft.server.v1_15_R1.EntityInsentient;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.util.List;

public class Navigator {
    private List<Entity> pathfinding;

    public void walk(Entity entity, Location end, double speed) {
        ((EntityInsentient) ((CraftEntity) entity).getHandle()).getNavigation().a(end.getX(), end.getY(), end.getZ(), speed);
    }
}
