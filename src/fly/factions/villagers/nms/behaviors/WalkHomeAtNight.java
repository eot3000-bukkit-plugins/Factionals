package fly.factions.villagers.nms.behaviors;

import com.google.common.collect.ImmutableMap;
import fly.factions.Factionals;
import fly.factions.model.PlotLocation;
import fly.factions.utils.TownUtils;
import fly.factions.villagers.Village;
import fly.factions.villagers.VillagerInfo;
import fly.factions.villagers.structures.HousingStructure;
import fly.factions.villagers.structures.Structure;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.block.data.type.Bed;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftVillager;
import org.bukkit.material.MaterialData;

import java.util.Optional;

public class WalkHomeAtNight extends Behavior<EntityVillager> {
    public WalkHomeAtNight() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), 0, 100);
    }

    //Should go
    @Override
    protected boolean g(WorldServer var0, EntityVillager var1, long var2) {
        return true;
    }

    //Start
    @Override
    protected void a(WorldServer var0, EntityVillager var1, long var2) {
        System.out.println("ran");

        var1.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, new MemoryTarget(new BlockPosition(792.0,75.0,12317), 2, 1));
    }

    public boolean invalidCurrentBed(VillagerInfo info) {
        Optional<MemoryTarget> target = ((CraftVillager) info.getEntity())
                .getHandle()
                .getBehaviorController()
                .getMemory(MemoryModuleType.WALK_TARGET);

        if(!target.isPresent()) {
            return false;
        }

        Vec3D vec = target.get().a().b();
        MaterialData data = new Location(info.getEntity().getWorld(), vec.x, vec.y, vec.z).getBlock().getState().getData();

        return info.getSleeping() == null && (data instanceof Bed && ((Bed) data).isOccupied());
    }
}
