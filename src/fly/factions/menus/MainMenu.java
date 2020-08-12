package fly.factions.menus;

import javafx.util.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MainMenu extends Menu {
    public static final MainMenu MAIN_MENU = new MainMenu();

    private MainMenu() {
        super("Main Menu");

        set(0, new Pair<>((t, p) -> open(FactionMenu.FACTION_MENU, p), withName(new ItemStack(Material.RED_STAINED_GLASS_PANE), "&cFactions Menu")));
        /*set(1, new Pair<>((t, p) -> {}, withName(new ItemStack(Material.LIME_STAINED_GLASS_PANE), "&aCompanies Menu")));
        set(2, new Pair<>((t, p) -> {}, withName(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), "&ePlot Menu")));*/
    }
}
