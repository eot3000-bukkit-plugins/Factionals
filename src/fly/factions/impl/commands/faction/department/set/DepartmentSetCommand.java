package fly.factions.impl.commands.faction.department.set;

import fly.factions.api.commands.CommandDivision;
import fly.factions.impl.commands.faction.department.set.leader.DepartmentSetLeaderCommand;
import fly.factions.impl.commands.faction.department.set.permission.DepartmentSetPermissionCommand;

public class DepartmentSetCommand extends CommandDivision {
    public DepartmentSetCommand() {
        //addHelpEntry("/f region set format fill <region> <fillRed> <fillGreen> <fillBlue> <fillOpacity>", "Set the region dynmap fill format");

        //addHelpEntry("/f region set format border <region> <borderRed> <borderGreen> <borderBlue>", "Set the region dynmap border color");

        addHelpEntry("/f department set leader <department> <user>", "Set the department leader");

        addHelpEntry("/f department set permission <department> <permission> <on | off>", "Set a department's permission");


        addSubCommand("leader", new DepartmentSetLeaderCommand());

        addSubCommand("permission", new DepartmentSetPermissionCommand());
    }
}
