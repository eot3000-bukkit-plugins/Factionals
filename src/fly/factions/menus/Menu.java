package fly.factions.menus;

import fly.factions.Factionals;
import fly.factions.model.Faction;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public abstract class Menu {
    static {
        start();
    }

    public static void init() {

    }

    private static void start() {
        MENU_NAMESPACE = new NamespacedKey(Factionals.getFactionals(), "menu");
        UUID_NAMESPACE = new NamespacedKey(Factionals.getFactionals(), "uuid");
        TYPE_NAMESPACE = new NamespacedKey(Factionals.getFactionals(), "type");
        FACTION_NAMESPACE = new NamespacedKey(Factionals.getFactionals(), "faction");
        WORLD_NAMESPACE = new NamespacedKey(Factionals.getFactionals(), "world");
        X_NAMESPACE = new NamespacedKey(Factionals.getFactionals(), "x");
        Y_NAMESPACE = new NamespacedKey(Factionals.getFactionals(), "y");
        Z_NAMESPACE = new NamespacedKey(Factionals.getFactionals(), "z");
        NUMBER_NAMESPACE = new NamespacedKey(Factionals.getFactionals(), "number-namespace");

        MENUS = new HashMap<>();

        File file = new File("./plugins/Factionals/menus.yml");

        if(!file.exists()) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(file);
                 InputStream stream = Menu.class.getClassLoader().getResourceAsStream("menus.yml")) {
                byte[] bytes = new byte[stream.available()];

                stream.read(bytes);

                fileOutputStream.write(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection menus = configuration.getConfigurationSection("menus");
        Set<String> keys = menus.getKeys(false);

        for(String key : keys) {
            MENUS.put(key, new FileMenu(menus.getConfigurationSection(key), key));
        }

        initMenus2();
    }

    private static void initMenus2() {
        MENUS.put("current-members", new MembersListMenu());
        MENUS.put("invite-members", new InviteMenu());
        MENUS.put("faction-top", new FactionTopMenu());
        MENUS.put("plot-perms", new PlotPermissionMenu());
        MENUS.put("banners", new BannerMenu());
        MENUS.put("regions", new RegionsMenu());

        MENUS.put("colors1", new ColorsMenu(0));
        MENUS.put("colors2", new ColorsMenu(1));
        MENUS.put("colors3", new ColorsMenu(2));
        MENUS.put("colors4", new ColorsMenu(3));
        MENUS.put("colors5", new ColorsMenu(4));
        MENUS.put("colors6", new ColorsMenu(5));

        CustomButton.BUTTONS.put("yourself", new YourselfButton());
        CustomButton.BUTTONS.put("claim-on", new ClaimOnButton());
    }

    public static NamespacedKey MENU_NAMESPACE;
    public static NamespacedKey UUID_NAMESPACE;
    public static NamespacedKey TYPE_NAMESPACE;
    public static NamespacedKey FACTION_NAMESPACE;
    public static NamespacedKey WORLD_NAMESPACE;
    public static NamespacedKey X_NAMESPACE;
    public static NamespacedKey Y_NAMESPACE;
    public static NamespacedKey Z_NAMESPACE;
    public static NamespacedKey NUMBER_NAMESPACE;

    private static Map<String, Menu> MENUS;

    public static void openMenu(Player player, String info) {
        Menu menu = MENUS.get(info.split("_")[0]);
        Inventory inv = menu.createInventory(player, info);

        Bukkit.getScheduler().runTaskLater(Factionals.getFactionals(), () -> player.openInventory(inv), 1);
    }

    public static void buttonClicked(InventoryClickEvent event, String id) {
        if(id.equalsIgnoreCase("no-action")) {
            return;
        }

        MENUS.get(id).runButtonClick(event);

        event.setCancelled(true);
    }

    public abstract void runButtonClick(InventoryClickEvent event);

    public abstract Inventory createInventory(Player player, String info);

    public static abstract class CustomButton {
        static final HashMap<String, CustomButton> BUTTONS = new HashMap<>();

        public abstract ItemStack getItemStack(Player player);

        public void runButtonClick(Player player) {

        }

        public static CustomButton getButton(String name) {
            return BUTTONS.get(name);
        }
    }

    public static class ButtonAction {
        public final ButtonActionType type;
        public final String info;

        public ButtonAction(ConfigurationSection section) {
            this.type = ButtonActionType.getFromString(section.getString("action.type"));
            this.info = section.contains("action.info") ? section.getString("action.info") : "";
        }

        public void execute(Player player) {
            if(type == null) {
                return;
            }

            type.biConsumer.accept(player, info);
        }

        public enum ButtonActionType {
            OPEN_MENU("open-menu", Menu::openMenu),
            CREATE_FACTION("create-faction", Faction::startCreation);

            public final String name;
            public final BiConsumer<Player, String> biConsumer;

            ButtonActionType(String name, BiConsumer<Player, String> biConsumer) {
                this.name = name;
                this.biConsumer = biConsumer;
            }

            public static ButtonActionType getFromString(String name) {
                for(ButtonActionType type : values()) {
                    if(type.name.equalsIgnoreCase(name)) {
                        return type;
                    }
                }

                return null;
            }
        }
    }
}
