package fly.factions.villagers;

import fly.factions.model.Plot;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VillagerInfo {
    private UUID uuid;

    private FactionalsVillagerProfession profession;
    private Map<EducationType, Integer> education = new HashMap<>();
    private Location bed;
    private Plot workLand;

    public FactionalsVillagerProfession getProfession() {
        return profession;
    }

    public Location getBed() {
        return bed;
    }

    public Map<EducationType, Integer> getEducation() {
        return new HashMap<>(education);
    }

    public Plot getWorkLand() {
        return workLand;
    }

    public enum EducationType {
        LITERACY,

        FISHING,
        FARMING,
        COOKING,
        HERDING,

        SMITHING,
        FLETCHING,

        MANAGEMENT,

        MAGIC,

        MELEE,
        ARCHERY
    }
}
