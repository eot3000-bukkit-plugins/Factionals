package fly.factions.impl.commands.faction.department.set.permission;

import fly.factions.api.commands.CommandDivision;
import fly.factions.api.model.ExecutiveDivision;
import fly.factions.api.model.Faction;
import fly.factions.api.model.User;
import fly.factions.api.permissions.FactionPermission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DepartmentSetPermissionCommand extends CommandDivision {
    public DepartmentSetPermissionCommand() {
        addHelpEntry("/f department set permission <department> <permission> <on | off>", "Set a department's permission");


        addSubCommand("*", this);
    }

    public boolean run(CommandSender sender, String division, String permission, String on) {
        if(ArgumentType.checkAll(sender, new String[] {permission}, ArgumentType.FACTION_PERMISSION)) {
            User user = USERS.get(Bukkit.getPlayer(sender.getName()).getUniqueId());
            Faction faction = user.getFaction();
            ExecutiveDivision divisionr = faction.getDepartment(division);

            FactionPermission factionPermission = FactionPermission.valueOf(permission);

            if(divisionr == null) {
                sender.sendMessage(ChatColor.RED + "ERROR: the department " + ChatColor.YELLOW + division + ChatColor.LIGHT_PURPLE + " does not exist");

                return false;
            }

            if(on.equalsIgnoreCase("on")) {
                divisionr.addPermission(factionPermission);

                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Successfully enabled permission " + ChatColor.YELLOW + permission + ChatColor.LIGHT_PURPLE + " for department " + ChatColor.YELLOW + division);
            } else {
                divisionr.removePermission(factionPermission);

                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Successfully disabled permission " + ChatColor.YELLOW + permission + ChatColor.LIGHT_PURPLE + " for department " + ChatColor.YELLOW + division);
            }

            return true;
        }

        return false;
    }
}
