package fly.factions.impl.commands.faction.region.set.leader;

import fly.factions.api.commands.CommandDivision;
import fly.factions.api.model.Faction;
import fly.factions.api.model.Region;
import fly.factions.api.model.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RegionSetLeaderCommand extends CommandDivision {
    public RegionSetLeaderCommand() {
        addHelpEntry("/f region set leader <region> <user>", "Set the region leader");


        addSubCommand("*", this);
    }

    @SuppressWarnings({"Pain please tell me what the NPE supression is", "unused", "deprecation"})
    public boolean run(CommandSender sender, String region, String newUser) {
        if(CommandDivision.ArgumentType.checkAll(sender, new String[] {newUser}, ArgumentType.USER)) {
            User user = USERS.get(Bukkit.getPlayer(sender.getName()).getUniqueId());
            Faction faction = user.getFaction();
            Region regionr = faction.getRegion(region);

            User victim = USERS.get(Bukkit.getOfflinePlayer(newUser).getUniqueId());

            if(regionr == null) {
                sender.sendMessage(ChatColor.RED + "ERROR: the region " + ChatColor.YELLOW + region + ChatColor.LIGHT_PURPLE + " does not exist");

                return false;
            }

            if(!victim.getFaction().equals(faction)) {
                sender.sendMessage(ChatColor.RED + "ERROR: this user is not in your faction");

                return false;
            }

            if(faction.getLeader().equals(user)) {
                regionr.setLeader(victim);

                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Successfully set region leader to " + ChatColor.YELLOW + newUser);

                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "ERROR: you do not have permission to run that command");

                return false;
            }
        }

        return false;
    }
}
