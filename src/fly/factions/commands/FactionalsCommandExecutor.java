package fly.factions.commands;

import fly.factions.Factionals;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public class FactionalsCommandExecutor implements CommandExecutor {
    protected Factionals factionals = Factionals.getFactionals();
    protected List<SubCommand> subCommands = new ArrayList<>();
    protected Random random = new Random();

    protected void registerSubCommand(int length, Function<CommandInfo, Boolean> function, String... args) {
        SubCommand command = new SubCommand();

        Predicate<CommandInfo> predicate = (info) -> info.args.length == length;

        int count = 0;
        for(String arg : args) {
            int x = count;
            predicate = predicate.and((info) -> info.args[x].equalsIgnoreCase(arg));
            count++;
        }

        command.predicate = predicate;
        command.function = function;

        subCommands.add(command);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        for(SubCommand subCommand : subCommands) {
            CommandInfo commandInfo = new CommandInfo();
            commandInfo.args = strings;
            commandInfo.executor = commandSender;

            if(subCommand.predicate.test(commandInfo)) {
                return subCommand.function.apply(commandInfo);
            }
        }
        commandSender.sendMessage(ChatColor.RED + "Not a real command!");
        return false;
    }
}
