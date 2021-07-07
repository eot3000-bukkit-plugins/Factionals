package fly.factions.impl.commands.faction.invite;

import fly.factions.api.commands.CommandDivision;
import fly.factions.impl.commands.faction.invite.add.InviteAddCommand;

public class InviteCommand extends CommandDivision {
    public InviteCommand() {
        addHelpEntry("/f invite add <user>", "Invite a user to the faction");

        addSubCommand("add", new InviteAddCommand());
    }
}
