package fly.factions.villagers.nms.entities;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import fly.factions.villagers.nms.behaviors.WalkHomeAtNight;
import net.minecraft.server.v1_15_R1.*;

public class FactionalsEntityVillager extends net.minecraft.server.v1_15_R1.EntityVillager {

    public FactionalsEntityVillager(EntityTypes<EntityVillager> types, World world) {
        super(types, world);
    }

    @Override
    public BehaviorController<?> a(Dynamic<?> dynamic) {
        BehaviorController<EntityVillager> behaviorController = new BehaviorController<>(Lists.newArrayList(MemoryModuleType.WALK_TARGET, MemoryModuleType.PATH), Lists.newArrayList(), dynamic);

        initBehaviors(behaviorController);

        return behaviorController;
    }

    @Override
    public void a(WorldServer world) {
        initBehaviors(this.getBehaviorController());
    }

    public void initBehaviors(BehaviorController<EntityVillager> controller) {
        controller.setSchedule(Schedule.VILLAGER_DEFAULT);

        controller.a(Activity.CORE, ImmutableList.of(new Pair<>(10, new BehavorMove(1))));
        controller.a(Activity.REST, ImmutableList.of(new Pair<>(10, new WalkHomeAtNight())));

        controller.a(ImmutableSet.of(Activity.CORE));
        controller.b(Activity.IDLE);
        controller.a(Activity.IDLE);
        controller.a(this.world.getDayTime(), this.world.getTime());
    }
}
