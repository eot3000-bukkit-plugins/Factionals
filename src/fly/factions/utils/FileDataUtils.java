package fly.factions.utils;

import fly.factions.Factionals;
import fly.factions.model.*;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.*;

public class FileDataUtils {
    private static File USERS = new File("plugins/Factionals/users.txt");
    private static File GROUPS = new File("plugins/Factionals/groups");
    private static File PLOTS = new File("plugins/Factionals/plots");

    public static void loadUsers() {
        GROUPS.mkdirs();
        PLOTS.mkdirs();

        try {
            USERS.createNewFile();

            Files.lines(USERS.toPath()).forEach(FileDataUtils::loadUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadUser(String s) {
        Factionals.getFactionals().addUser(new User(UUID.fromString(s)));
    }

    public static void loadGroups() {
        for (File file : GROUPS.listFiles()) {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

            PlayerGroup.createNew(configuration);
        }
    }

    public static void loadPlots() {
        for (File file : PLOTS.listFiles()) {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

            Plot.createNew(configuration);
        }
    }

    public static void saveUsers() {
        try (FileOutputStream stream = new FileOutputStream(USERS)) {
            for (User user : Factionals.getFactionals().getUsers()) {
                stream.write((user.getUuid().toString() + "\n").getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveGroups() {
        for (PlayerGroup group : Factionals.getFactionals().getGroups()) {
            saveGroup(group);
        }
    }

    public static void saveGroup(PlayerGroup group) {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.set("group", group.saveInfo());
            config.save(new File(GROUPS.getPath() + "/" + group.getName().toLowerCase()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void savePlots() {
        for (Plot plot : Factionals.getFactionals().getPlots()) {
            savePlot(plot);
        }
    }

    public static void savePlot(Plot plot) {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.set("plot", plot.saveInfo());
            config.save(new File(PLOTS.getPath() + "/" + plot.getLocation().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
