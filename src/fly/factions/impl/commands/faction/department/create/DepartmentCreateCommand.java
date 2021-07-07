package fly.factions.impl.commands.faction.department.create;

import fly.factions.api.commands.CommandDivision;
import fly.factions.api.model.User;
import fly.factions.impl.model.ExecutiveDivisionImpl;
import fly.factions.impl.model.RegionImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DepartmentCreateCommand extends CommandDivision {
    public DepartmentCreateCommand() {
        addHelpEntry("/f department create <name>", "Create a department with the given name");


        addSubCommand("*", this);
    }

    public boolean run(CommandSender sender, String name) {
        User user = USERS.get(Bukkit.getPlayer(sender.getName()).getUniqueId());

        if(user.getFaction().getDepartment(name) != null) {
            user.sendMessage(ChatColor.RED + "ERROR: the department" + ChatColor.YELLOW + name + ChatColor.RED + " already exists");
            return false;
        }

        user.getFaction().addDepartment(new ExecutiveDivisionImpl(name, user, user.getFaction()));

        user.sendMessage(ChatColor.LIGHT_PURPLE + "Successfully created department " + ChatColor.YELLOW + name);
        return true;
    }
}
