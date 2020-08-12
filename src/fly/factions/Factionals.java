package fly.factions;

import com.massivecraft.factions.integration.Econ;
import fly.factions.menus.MainMenu;
import fly.factions.menus.Menu;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Factionals extends JavaPlugin implements Listener {
    private Economy economy;

    private static Factionals FACTIONALS;
    private Logger logger = Bukkit.getLogger();

    public Factionals() {
        FACTIONALS = this;
    }

    @Override
    public void onEnable() {
        logger.info(ChatColor.DARK_AQUA + "---------------------------------------------");
        logger.info(ChatColor.AQUA + "Starting Factionals!");
        logger.info(ChatColor.DARK_AQUA + "---------------------------------------------");

        Bukkit.getPluginManager().registerEvents(this, this);

        economy = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Menu.open(MainMenu.MAIN_MENU, (Player) sender);
        return true;
    }

    @EventHandler
    public void onInventoryUse(InventoryClickEvent event) {
        Menu.onClick(event);
    }
}
