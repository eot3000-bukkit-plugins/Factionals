package fly.factions.serialization;

import fly.factions.model.Faction;
import fly.factions.model.User;
import javafx.util.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FactionSerializer extends Serializer<Faction> {
    private File dir = new File("plugins\\Factionals\\factions");

    public FactionSerializer() {
        super(Faction.class);
    }

    @Override
    public File dir() {
        return dir;
    }

    @Override
    public Faction load(File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        if(configuration.getBoolean("deleted")) {
            return null;
        }

        String name = configuration.getString("name");
        User user = factionals.getUserFromUUID(UUID.fromString(configuration.getString("leader")));
        Faction faction = new Faction(name, user, configuration.getLong("creation-date"));

        for(String uuid : configuration.getStringList("members")) {
            faction.addMember(factionals.getUserFromUUID(UUID.fromString(uuid)));
        }

        for(String x : configuration.getConfigurationSection("plots").getKeys(false)) {
            ConfigurationSection section = configuration.getConfigurationSection("plots").getConfigurationSection(x);
            int[] info = new int[32];
            String perms = section.getString("perms");
            int location = Integer.parseInt(x);

            for(int i = 0; i < 32; i++) {
                info[i] = section.getInt("" + x);
            }

            factionals.setPlot(location, faction);

            faction.setPlot(location, new Pair<>(perms, info));
        }

        return faction;
    }

    @Override
    public void save(Faction savable) {
        YamlConfiguration config = new YamlConfiguration();
        List<String> members = new ArrayList<>();
        Map<Integer, Map<String, String>> plots = new HashMap<>();

        for (User member : savable.getMembers()) {
            members.add(member.getUuid().toString());
        }

        for (Map.Entry<Integer, Pair<String, int[]>> plot : savable.getPlots().entrySet()) {
            Map<String, String> plotMap = new HashMap<>();

            plotMap.put("perms", plot.getValue().getKey());

            for(int x = 0; x < 32; x++) {
                plotMap.put("" + x, "" + plot.getValue().getValue()[x]);
            }

            plots.put(plot.getKey(), plotMap);
        }

        config.set("leader", ((User) savable.getLeader()).getUuid().toString());
        config.set("name", savable.getName());
        config.set("members", members);
        config.set("creation-date", savable.getCreationDate());
        config.set("deleted", savable.isDeleted());
        config.set("plots", plots);

        try {
            config.save(new File(dir.getPath() + "\\" + savable.getCreationDate()));
        } catch (IOException e) {
            //
        }
    }
}
