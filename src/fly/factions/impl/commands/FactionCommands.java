package fly.factions.impl.commands;

import fly.factions.api.commands.CommandDivision;
import fly.factions.impl.commands.faction.create.CreateCommand;
import fly.factions.impl.commands.faction.department.DepartmentCommand;
import fly.factions.impl.commands.faction.invite.InviteCommand;
import fly.factions.impl.commands.faction.region.RegionCommand;

public class FactionCommands extends CommandDivision {
    public FactionCommands() {
        super("f");

        addHelpEntry("/f", "Open this menu");

        addHelpEntry("/f create <name>", "Create a faction with the given name");

        addHelpEntry("/f invite", "View member invite commands");

        addHelpEntry("/f join <faction>", "Attempt to join the given faction");

        addHelpEntry("/f claim", "Claim one chunk at your location for your faction");

        addHelpEntry("/f claim fill", "Fills in a hollow-ly filled area with chunks");

        addHelpEntry("/f info", "View information about your faction");

        addHelpEntry("/f map", "View a map of nearby chunks and their factions");

        addHelpEntry("/f set", "View faction settings commands");

        addHelpEntry("/f region", "View region commands");

        addHelpEntry("/f town", "View town commands");

        addHelpEntry("/f department", "View department commands");


        addSubCommand("create", new CreateCommand());

        addSubCommand("invite", new InviteCommand());



        addSubCommand("region", new RegionCommand());

        addSubCommand("department", new DepartmentCommand());
    }
}
