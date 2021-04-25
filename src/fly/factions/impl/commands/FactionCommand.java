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

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "claim"))
                .parameter(Parameter.requireString("claim"))
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "claimFill"))
                .parameter(Parameter.requireString("claim"))
                .parameter(Parameter.requireString("fill"))
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "info"))
                .parameter(Parameter.requireString("info"))
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "map"))
                .parameter(Parameter.requireString("map"))
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

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "createDepartment"))
                .parameter(Parameter.requireString("department"))
                .parameter(Parameter.requireString("create"))
                .parameter(Parameter.STRING)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "addToDepartment"))
                .parameter(Parameter.requireString("department"))
                .parameter(Parameter.requireString("add"))
                .parameter(Parameter.STRING)
                .parameter(Parameter.USER)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "setDepartmentLeader"))
                .parameter(Parameter.requireString("department"))
                .parameter(Parameter.requireString("set"))
                .parameter(Parameter.requireString("leader"))
                .parameter(Parameter.STRING)
                .parameter(Parameter.USER)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "createRegion"))
                .parameter(Parameter.requireString("region"))
                .parameter(Parameter.requireString("create"))
                .parameter(Parameter.STRING)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "addToRegion"))
                .parameter(Parameter.requireString("region"))
                .parameter(Parameter.requireString("add"))
                .parameter(Parameter.STRING)
                .parameter(Parameter.USER)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "setRegionLeader"))
                .parameter(Parameter.requireString("region"))
                .parameter(Parameter.requireString("set"))
                .parameter(Parameter.requireString("leader"))
                .parameter(Parameter.STRING)
                .parameter(Parameter.USER)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "setFactionFillColor"))
                .parameter(Parameter.requireString("set"))
                .parameter(Parameter.requireString("fillColor"))
                .parameter(Parameter.INTEGER)
                .parameter(Parameter.INTEGER)
                .parameter(Parameter.INTEGER)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "setFactionFillOpacity"))
                .parameter(Parameter.requireString("set"))
                .parameter(Parameter.requireString("fillOpacity"))
                .parameter(Parameter.DOUBLE)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "setFactionBorderColor"))
                .parameter(Parameter.requireString("set"))
                .parameter(Parameter.requireString("borderColor"))
                .parameter(Parameter.INTEGER)
                .parameter(Parameter.INTEGER)
                .parameter(Parameter.INTEGER)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "setRegionFillColor"))
                .parameter(Parameter.requireString("region"))
                .parameter(Parameter.requireString("set"))
                .parameter(Parameter.requireString("fillColor"))
                .parameter(Parameter.STRING)
                .parameter(Parameter.INTEGER)
                .parameter(Parameter.INTEGER)
                .parameter(Parameter.INTEGER)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "setRegionFillOpacity"))
                .parameter(Parameter.requireString("region"))
                .parameter(Parameter.requireString("set"))
                .parameter(Parameter.requireString("fillOpacity"))
                .parameter(Parameter.STRING)
                .parameter(Parameter.DOUBLE)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(FactionImpl.class, "setRegionBorderColor"))
                .parameter(Parameter.requireString("region"))
                .parameter(Parameter.requireString("set"))
                .parameter(Parameter.requireString("borderColor"))
                .parameter(Parameter.STRING)
                .parameter(Parameter.INTEGER)
                .parameter(Parameter.INTEGER)
                .parameter(Parameter.INTEGER)
                .build());

        this.factionals = factionals;

        factionals.getCommand("f").setExecutor(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
