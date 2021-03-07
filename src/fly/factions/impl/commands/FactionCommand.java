package fly.factions.impl.commands;

import fly.factions.Factionals;
import fly.factions.api.commands.CommandRegister;
import fly.factions.impl.model.FactionImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class FactionCommand extends CommandRegister {
    private Factionals factionals;

    public FactionCommand(Factionals factionals) {
        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "createFaction"))
                .parameter(Parameter.requireString("create"))
                .parameter(Parameter.STRING)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "createRegion"))
                .parameter(Parameter.requireString("createRegion"))
                .parameter(Parameter.STRING)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "createDepartment"))
                .parameter(Parameter.requireString("createDepartment"))
                .parameter(Parameter.STRING)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "claim"))
                .parameter(Parameter.requireString("claim"))
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "info"))
                .parameter(Parameter.requireString("info"))
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "inviteAdd"))
                .parameter(Parameter.requireString("invite"))
                .parameter(Parameter.requireString("add"))
                .parameter(Parameter.USER)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "join"))
                .parameter(Parameter.requireString("join"))
                .parameter(Parameter.FACTION)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "addToDepartment"))
                .parameter(Parameter.requireString("department"))
                .parameter(Parameter.requireString("add"))
                .parameter(Parameter.STRING)
                .parameter(Parameter.USER)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "setDepartmentLeader"))
                .parameter(Parameter.requireString("department"))
                .parameter(Parameter.requireString("setLeader"))
                .parameter(Parameter.STRING)
                .parameter(Parameter.USER)
                .build());

        this.factionals = factionals;

        factionals.getCommand("f").setExecutor(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
