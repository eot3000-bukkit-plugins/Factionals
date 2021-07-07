package fly.factions.impl.commands.faction.create;

import fly.factions.api.commands.CommandDivision;
import fly.factions.api.model.Faction;
import fly.factions.api.model.User;
import fly.factions.impl.model.FactionImpl;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends CommandDivision {
    public CreateCommand() {
        addHelpEntry("/f create <name>", "Create a faction with the given name");

        addSubCommand("*", this);
    }

    @SuppressWarnings({"unused"})
    public boolean run(CommandSender sender, String name) {
        if(CommandDivision.ArgumentType.checkAll(sender, new String[] {name}, ArgumentType.NOT_FACTION)) {
            User user = USERS.get(((Player) sender).getUniqueId());

            if(user.getFaction() != null) {
                user.sendMessage(ChatColor.RED + "ERROR: you must leave your faction before creating a new one");

                return false;
            }

            Faction faction = new FactionImpl(user, name);

            FACTIONS.set(name, faction);

            API.broadcast(ChatColor.LIGHT_PURPLE + "New faction created: " + ChatColor.YELLOW + name);

            return true;
        }

        return false;
    }
}
