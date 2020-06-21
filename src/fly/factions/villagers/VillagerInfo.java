package fly.factions.villagers;

import fly.factions.Factionals;
import fly.factions.model.Plot;
import fly.factions.model.PlotLocation;
import fly.factions.utils.TownUtils;
import fly.factions.villagers.structures.HousingStructure;
import org.bukkit.Location;
import org.bukkit.entity.Villager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VillagerInfo {
    private FactionalsVillagerProfession profession;
    private Villager entity;
    private Village village;
    private Map<EducationType, Integer> education = new HashMap<>();

    private Location sleeping;


    public VillagerInfo(Villager villager, FactionalsVillagerProfession profession) {
        this.profession = profession;
        this.entity = villager;

        findVillage();

        Factionals.getFactionals().addVillagerInfo(villager.getUniqueId(), this);
    }

    private void findVillage() {
        Plot plot = Factionals.getFactionals().getPlotByLocation(new PlotLocation(entity.getChunk()));
        Village village = TownUtils.getVillage(plot);

        if(village == null) {
            return;
        }

        this.village = village;
    }

    public FactionalsVillagerProfession getProfession() {
        return profession;
    }

    public Map<EducationType, Integer> getEducation() {
        return new HashMap<>(education);
    }

    public Villager getEntity() {
        return entity;
    }

    public Village getVillage() {
        return village;
    }

    public Location getSleeping() {
        return sleeping;
    }

    public void setSleeping(Location sleeping) {
        this.sleeping = sleeping;
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
