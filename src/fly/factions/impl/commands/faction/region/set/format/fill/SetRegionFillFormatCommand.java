package fly.factions.impl.commands.faction.region.set.format.fill;

import fly.factions.api.commands.CommandDivision;
import fly.factions.api.model.Faction;
import fly.factions.api.model.Region;
import fly.factions.api.model.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;

public class SetRegionFillFormatCommand extends CommandDivision {
    public SetRegionFillFormatCommand() {
        addHelpEntry("/f region set format fill <region> <fillRed> <fillGreen> <fillBlue> <fillOpacity>", "Set the region dynmap fill format");


        addSubCommand("*", this);
    }

    @SuppressWarnings({"Pain please tell me what the NPE supression is", "unused"})
    public boolean run(CommandSender sender, String region, String r, String g, String b, String o) {
        if(CommandDivision.ArgumentType.checkAll(sender, new String[] {r, g, b, o}, ArgumentType.INT, ArgumentType.INT, ArgumentType.INT, ArgumentType.DOUBLE)) {
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

                double od = constrain(0.25, 0.75, Double.parseDouble(o));

                regionr.setFillColor(Color.fromRGB(ri, gi, bi));
                regionr.setFillOpacity(od);

                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Successfully set fill format to " + ChatColor.YELLOW + ri + "," + gi + "," + bi + "; " + od);

                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "ERROR: you do not have permission to run that command");

                return false;
            }
        }

        return false;
    }
}
