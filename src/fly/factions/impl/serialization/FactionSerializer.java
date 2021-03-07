package fly.factions.impl.serialization;

import fly.factions.Factionals;
import fly.factions.api.model.ExecutiveDivision;
import fly.factions.api.model.Faction;
import fly.factions.api.model.Region;
import fly.factions.api.model.User;
import fly.factions.api.registries.Registry;
import fly.factions.api.serialization.Serializer;
import fly.factions.impl.model.ExecutiveDivisionImpl;
import fly.factions.impl.model.FactionImpl;
import fly.factions.impl.model.RegionImpl;
import javafx.util.Pair;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FactionSerializer extends Serializer<Faction> {
    private File dir = new File("plugins\\Factionals\\factions");

    public FactionSerializer(Factionals factionals) {
        super(Faction.class, factionals);
    }

    @Override
    public File dir() {
        return dir;
    }

    @Override
    public Faction load(File file) {
        Registry<User, UUID> r = factionals.getRegistry(User.class, UUID.class);

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        if(!configuration.getBoolean("deleted")) {
            Faction faction = new FactionImpl(r.get(UUID.fromString(configuration.getString("leader"))), configuration.getString("name"));

            //Departments

            ConfigurationSection departments = configuration.getConfigurationSection("departments");

            for (String string : departments.getKeys(false)) {
                ConfigurationSection department = departments.getConfigurationSection(string);
                ExecutiveDivision division = new ExecutiveDivisionImpl(department.getString("name"), r.get(UUID.fromString(department.getString("leader"))), faction);

                for (String member : department.getStringList("members")) {
                    division.addMember(r.get(UUID.fromString(member)));
                }

                faction.addDepartment(division);
            }

            //Regions

            ConfigurationSection regions = configuration.getConfigurationSection("regions");

            for (String string : regions.getKeys(false)) {
                ConfigurationSection region = regions.getConfigurationSection(string);
                Region factionRegion = new RegionImpl(region.getString("name"), r.get(UUID.fromString(region.getString("leader"))), faction);

                for (String member : region.getStringList("members")) {
                    factionRegion.addMember(r.get(UUID.fromString(member)));
                }

                faction.addRegion(factionRegion);
            }

            //Members

            for (String member : configuration.getStringList("members")) {
                r.get(UUID.fromString(member)).setFaction(faction);
            }

            return faction;
        }

        return null;
    }

    @Override
    public void save(Faction faction) {
        File file = new File("plugins\\Factionals\\factions\\" + faction.getName());

        YamlConfiguration configuration = new YamlConfiguration();

        Map<String, Map<String, Object>> departments = new HashMap<>();
        Map<String, Map<String, Object>> regions = new HashMap<>();
        List<String> members = new ArrayList<>();

        //Departments

        for(ExecutiveDivision division : faction.getDepartments()) {
            Map<String, Object> department = new HashMap<>();

            List<String> departmentMembers = new ArrayList<>();

            for(User user : division.getMembers()) {
                departmentMembers.add(user.getUniqueId().toString());
            }

            department.put("members", departmentMembers);
            department.put("name", division.getName());
            department.put("leader", division.getLeader().getUniqueId().toString());

            departments.put(division.getName(), department);
        }

        //Regions

        for(Region region : faction.getRegions()) {
            Map<String, Object> factionRegion = new HashMap<>();

            List<String> regionMembers = new ArrayList<>();

            for(User user : region.getMembers()) {
                regionMembers.add(user.getUniqueId().toString());
            }

            factionRegion.put("members", regionMembers);
            factionRegion.put("name", region.getName());
            factionRegion.put("leader", region.getLeader().getUniqueId().toString());

            regions.put(region.getName(), factionRegion);
        }

        //Members

        for(User user : faction.getMembers()) {
            members.add(user.getUniqueId().toString());
        }

        configuration.set("departments", departments);
        configuration.set("regions", regions);
        configuration.set("members", members);
        configuration.set("leader", faction.getLeader().getUniqueId().toString());
        configuration.set("name", faction.getName());
        configuration.set("deleted", faction.isDeleted());

        try {
            configuration.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
