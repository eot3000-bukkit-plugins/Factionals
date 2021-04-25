package fly.factions.impl.commands;

import fly.factions.Factionals;
import fly.factions.api.commands.CommandRegister;
import fly.factions.impl.model.FactionImpl;
import fly.factions.impl.util.Plots;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class PlotCommand extends CommandRegister {
    private Factionals factionals;

    public PlotCommand(Factionals factionals) {
        addSubCommand(new SubCommand.SubCommandBuilder(m(Plots.class, "plotForSale"))
                .parameter(Parameter.requireString("fs"))
                .parameter(Parameter.INTEGER)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(Plots.class, "plotNotForSale"))
                .parameter(Parameter.requireString("nfs"))
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(Plots.class, "buyPlot"))
                .parameter(Parameter.requireString("claim"))
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(Plots.class, "setRegion"))
                .parameter(Parameter.requireString("setRegion"))
                .parameter(Parameter.STRING)
                .build());

        addSubCommand(new SubCommand.SubCommandBuilder(m(Plots.class, "setPerm"))
                .parameter(Parameter.requireString("set"))
                .parameter(Parameter.requireString("permission"))
                .parameter(Parameter.STRING)
                .parameter(Parameter.STRING)
                .parameter(Parameter.BOOLEAN)
                .build());

        this.factionals = factionals;

        factionals.getCommand("plot").setExecutor(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
