package fly.factions.impl.commands;

import fly.factions.Factionals;
import fly.factions.api.commands.CommandRegister;
import fly.factions.impl.util.LotCommands;
import fly.factions.impl.util.Plots;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class PlotCommand extends CommandRegister {
    private Factionals factionals;

    public PlotCommand(Factionals factionals) {
        addSubCommand(new SubCommand.SubCommandBuilder(m(LotCommands.class, "plotForSale"))
                .parameter(Parameter.requireString("fs"))
                .parameter(Parameter.INTEGER)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(LotCommands.class, "plotNotForSale"))
                .parameter(Parameter.requireString("nfs"))
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(LotCommands.class, "buyPlot"))
                .parameter(Parameter.requireString("claim"))
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(LotCommands.class, "info"))
                .parameter(Parameter.requireString("info"))
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(Plots.class, "setRegion"))
                .parameter(Parameter.requireString("setRegion"))
                .parameter(Parameter.STRING)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(LotCommands.class, "setPerm"))
                .parameter(Parameter.requireString("set"))
                .parameter(Parameter.requireString("permission"))
                .parameter(Parameter.STRING)
                .parameter(Parameter.STRING)
                .parameter(Parameter.BOOLEAN)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(LotCommands.class, "setLot"))
                .parameter(Parameter.requireString("set"))
                .parameter(Parameter.requireString("lot"))
                .parameter(Parameter.INTEGER)
                .parameter(Parameter.INTEGER)
                .parameter(Parameter.INTEGER)
                .parameter(Parameter.INTEGER)
                .parameter(Parameter.INTEGER)
                .parameter(Parameter.STRING)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(LotCommands.class, "setTown"))
                .parameter(Parameter.requireString("set"))
                .parameter(Parameter.requireString("town"))
                .parameter(Parameter.STRING)
                .parameter(Parameter.STRING)
                .parameter(Parameter.INTEGER)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(LotCommands.class, "createLot"))
                .parameter(Parameter.requireString("create"))
                .parameter(Parameter.requireString("lot"))
                .parameter(Parameter.STRING)
                .build());

        this.factionals = factionals;

        factionals.getCommand("plot").setExecutor(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
