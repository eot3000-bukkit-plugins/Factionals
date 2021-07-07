package fly.factions.impl.commands.faction.region.create;

import fly.factions.api.commands.CommandDivision;
import fly.factions.api.model.User;
import fly.factions.impl.model.RegionImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RegionCreateCommand extends CommandDivision {
    public RegionCreateCommand() {
        addHelpEntry("/f region create <name>", "Create a region with the given name");


        addSubCommand("*", this);
    }

    public boolean run(CommandSender sender, String name) {
        User user = USERS.get(Bukkit.getPlayer(sender.getName()).getUniqueId());

        if(user.getFaction().getRegion(name) != null) {
            user.sendMessage(ChatColor.RED + "ERROR: the region " + ChatColor.YELLOW + name + ChatColor.RED + " already exists");
            return false;
        }

        user.getFaction().addRegion(new RegionImpl(name, user, user.getFaction()));

        user.sendMessage(ChatColor.LIGHT_PURPLE + "Successfully created region " + ChatColor.YELLOW + name);

        return true;
    }
}
