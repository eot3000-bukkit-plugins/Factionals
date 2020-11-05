package fly.factions.serialization;

import fly.factions.model.Faction;
import fly.factions.model.User;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        return faction;
    }

    @Override
    public void save(Faction savable) {
        YamlConfiguration config = new YamlConfiguration();
        List<String> members = new ArrayList<>();

        for (User member : savable.getMembers()) {
            members.add(member.getUuid().toString());
        }

        config.set("leader", ((User) savable.getLeader()).getUuid().toString());
        config.set("name", savable.getName());
        config.set("members", members);
        config.set("creation-date", savable.getCreationDate());
        config.set("deleted", savable.isDeleted());

        try {
            config.save(new File(dir.getPath() + "\\" + savable.getCreationDate()));
        } catch (IOException e) {
            //
        }
    }
}
