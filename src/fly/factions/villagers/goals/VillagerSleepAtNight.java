package fly.factions.villagers.goals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import fly.factions.Factionals;
import fly.factions.villagers.VillagerInfo;
import fly.factions.villagers.nms.Navigator;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;

import java.util.EnumSet;

public class VillagerSleepAtNight implements Goal {
    private Villager entity;
    private VillagerInfo info;
    private boolean activated;

    @Override
    public boolean shouldActivate() {
        return entity.getWorld().getTime() > 16000 && !activated;
    }

    @Override
    public GoalKey getKey() {
        return GoalKey.of(Villager.class, new NamespacedKey(Factionals.getFactionals(), "villager_sleep_at_night"));
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE);
    }

    @Override
    public void start() {
        activated = true;
        //Navigator.moveTo(entity, info.getBed(), 0.7);
    }
}
