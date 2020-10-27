package fly.factions.serialization;

import fly.factions.model.User;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class UserSerializer extends Serializer<User> {
    private File dir = new File("plugins\\Factionals\\users");

    public UserSerializer() {
        super(User.class);
    }

    @Override
    public File dir() {
        return dir;
    }

    @Override
    public User load(File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        return new User(UUID.fromString(configuration.getString("uuid")));
    }

    @Override
    public void save(User savable) {
        YamlConfiguration config = new YamlConfiguration();

        config.set("uuid", savable.getUuid().toString());

        try {
            config.save(new File(dir.getPath() + "\\" + savable.getUuid().toString()));
        } catch (IOException e) {
            //
        }
    }
}
