package fly.factions.impl.commands.faction.department.set.leader;

import fly.factions.api.commands.CommandDivision;
import fly.factions.api.model.ExecutiveDivision;
import fly.factions.api.model.Faction;
import fly.factions.api.model.Region;
import fly.factions.api.model.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DepartmentSetLeaderCommand extends CommandDivision {
    public DepartmentSetLeaderCommand() {
        addHelpEntry("/f department set leader <department> <user>", "Set the department leader");


        addSubCommand("*", this);
    }

    @SuppressWarnings({"Pain please tell me what the NPE supression is", "unused", "deprecation"})
    public boolean run(CommandSender sender, String division, String newUser) {
        if(ArgumentType.checkAll(sender, new String[] {newUser}, ArgumentType.USER)) {
            User user = USERS.get(Bukkit.getPlayer(sender.getName()).getUniqueId());
            Faction faction = user.getFaction();
            ExecutiveDivision divisionr = faction.getDepartment(division);

            User victim = USERS.get(Bukkit.getOfflinePlayer(newUser).getUniqueId());

            if(divisionr == null) {
                sender.sendMessage(ChatColor.RED + "ERROR: the department " + ChatColor.YELLOW + division + ChatColor.LIGHT_PURPLE + " does not exist");

                return false;
            }

            if(!victim.getFaction().equals(faction)) {
                sender.sendMessage(ChatColor.RED + "ERROR: this user is not in your faction");

                return false;
            }

            if(faction.getLeader().equals(user)) {
                divisionr.setLeader(victim);

                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Successfully set department leader to " + ChatColor.YELLOW + newUser);

                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "ERROR: you do not have permission to run that command");

                return false;
            }
        }

        return false;
    }
}
