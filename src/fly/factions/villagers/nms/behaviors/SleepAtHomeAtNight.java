package fly.factions.villagers.nms.behaviors;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.v1_15_R1.*;

public class SleepAtHomeAtNight extends Behavior<EntityVillager> {
    public SleepAtHomeAtNight() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT));
    }

    //Should go
    @Override
    protected boolean g(WorldServer var0, EntityVillager var1, long var2) {
        return var1.getBukkitCreature().getWorld().getTime() < 14000 && shouldRun(var1);
    }

    //Start
    @Override
    protected void a(WorldServer var0, EntityVillager var1, long var2) {

    }

    public boolean shouldRun(EntityVillager villager) {
        return true;
    }
}