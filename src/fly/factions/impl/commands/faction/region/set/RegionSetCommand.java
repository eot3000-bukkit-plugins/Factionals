package fly.factions.impl.commands.faction.region.set;

import fly.factions.api.commands.CommandDivision;
import fly.factions.impl.commands.faction.region.set.format.RegionSetFormatCommand;
import fly.factions.impl.commands.faction.region.set.leader.RegionSetLeaderCommand;

public class RegionSetCommand extends CommandDivision {
    public RegionSetCommand() {
        //addHelpEntry("/f region set format fill <region> <fillRed> <fillGreen> <fillBlue> <fillOpacity>", "Set the region dynmap fill format");

        //addHelpEntry("/f region set format border <region> <borderRed> <borderGreen> <borderBlue>", "Set the region dynmap border color");

        addHelpEntry("/f region set format", "View region format commands");

        addHelpEntry("/f region set leader <region> <user>", "Set the region leader");

        addSubCommand("format", new RegionSetFormatCommand());

        addSubCommand("leader", new RegionSetLeaderCommand());
    }
}
