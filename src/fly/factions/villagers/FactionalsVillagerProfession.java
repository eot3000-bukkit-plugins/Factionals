package fly.factions.villagers;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public enum FactionalsVillagerProfession {
    NONE(Villager.Profession.NONE),
    BABY(Villager.Profession.NONE),

    FARMER(Villager.Profession.FARMER),
    BUTCHER(Villager.Profession.BUTCHER),
    FISHERMAN(Villager.Profession.FISHERMAN),
    CHEF(Villager.Profession.LEATHERWORKER),

    SMITH(Villager.Profession.TOOLSMITH),

    CLERIC(Villager.Profession.CLERIC),

    TEACHER(Villager.Profession.CARTOGRAPHER),
    LIBRARIAN(Villager.Profession.LIBRARIAN),

    TRADER(Villager.Profession.NITWIT),
    MANAGER(Villager.Profession.CARTOGRAPHER),

    SOLDIER_MELEE(EntityType.VINDICATOR),
    SOLDIER_ARCHER(EntityType.PILLAGER),
    SOLDIER_POTIONS(EntityType.WITCH),
    SOLDIER_MAGIC(EntityType.EVOKER),
    SOLDIER_ILLUSIONS(EntityType.ILLUSIONER);

    private Villager.Profession profession;
    private EntityType type;
    private boolean normal;

    FactionalsVillagerProfession(Villager.Profession profession) {
        this.profession = profession;
        this.normal = true;
    }

    FactionalsVillagerProfession(EntityType type) {
        this.type = type;
    }

    public Entity createNew(Location location) {
        if(normal) {
            Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

            villager.setProfession(profession);

            return villager;
        } else {
            return location.getWorld().spawnEntity(location, type);
        }
    }
}
