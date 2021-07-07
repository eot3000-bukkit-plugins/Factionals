package fly.factions.impl.commands.faction.region.set.format.border;

import fly.factions.api.commands.CommandDivision;
import fly.factions.api.model.Faction;
import fly.factions.api.model.Region;
import fly.factions.api.model.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;

public class SetRegionBorderFormatCommand extends CommandDivision {
    public SetRegionBorderFormatCommand() {
        addHelpEntry("/f region set format border <region> <borderRed> <borderGreen> <borderBlue>", "Set the region dynmap border color");


        addSubCommand("*", this);
    }

    @SuppressWarnings({"Pain please tell me what the NPE supression is", "unused"})
    public boolean run(CommandSender sender, String region, String r, String g, String b) {
        if(CommandDivision.ArgumentType.checkAll(sender, new String[] {r, g, b}, ArgumentType.INT, ArgumentType.INT, ArgumentType.INT)) {
            User user = USERS.get(Bukkit.getPlayer(sender.getName()).getUniqueId());
            Faction faction = user.getFaction();
            Region regionr = faction.getRegion(region);

            if(regionr == null) {
                sender.sendMessage(ChatColor.RED + "ERROR: the region " + ChatColor.YELLOW + region + ChatColor.LIGHT_PURPLE + " does not exist");

                return false;
            }

            if(regionr.getLeader().equals(user) || faction.getLeader().equals(user)) {
                int ri = constrain(0, 255, Integer.parseInt(r));
                int gi = constrain(0, 255, Integer.parseInt(g));
                int bi = constrain(0, 255, Integer.parseInt(b));

                regionr.setBorderColor(Color.fromRGB(ri, gi, bi));

                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Successfully set border format to " + ChatColor.YELLOW + ri + "," + gi + "," + bi);

                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "ERROR: you do not have permission to run that command");

                return false;
            }
        }

        return false;
    }
}

