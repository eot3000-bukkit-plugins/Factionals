package fly.factions.commands;

import fly.factions.Factionals;
import fly.factions.model.Faction;
import fly.factions.model.Plot;
import fly.factions.permissions.GroupPermission;
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

    protected Predicate<String> pGroupNotNull = (x) -> factionals.getGroupByName(x) != null;
    protected Predicate<String> pGroupNull = (x) -> factionals.getGroupByName(x) == null;
    protected Predicate<String> pGroupNotFaction = (x) -> !(factionals.getGroupByName(x) instanceof Faction);
    protected Predicate<String> pGroupFaction = (x) -> factionals.getGroupByName(x) instanceof Faction;
    protected Predicate<String> pPlayerNotNull = (x) -> factionals.getUserByName(x) != null;

    protected Predicate<String> pGroupPermission = (x) -> {
        for(GroupPermission permission : GroupPermission.values()) {
            if(permission.normal.equalsIgnoreCase(x)) {
                return true;
            }
        }

        return false;
    };
    protected Predicate<String> pFactionPermission = (x) -> {
        for(GroupPermission permission : GroupPermission.values()) {
            if(permission.normal.equalsIgnoreCase(x)) {
                return true;
            }
        }

        return false;
    };
    protected Predicate<String> pPlotPermission = (x) -> {

        try {
            Plot.PlotPermission.valueOf(x.toUpperCase().replaceAll("-", "_"));
            return true;
        } catch (Exception e) {
            return false;
        }
    };

    protected String nameTaken = ChatColor.RED + "Name already taken!";
    protected String notAFaction = ChatColor.RED + "Not a faction!";
    protected String notAGroup = ChatColor.RED + "Not a group!";
    protected String notARank = ChatColor.RED + "Not a rank!";
    protected String notAUser = ChatColor.RED + "Not a user!";
    protected String notAPlot = ChatColor.RED + "Not a plot!";
    protected String notAPermission = ChatColor.RED + "Not a permission!";
    protected String noPermission = ChatColor.RED + "No permission!";
    protected String notEnoughMoney = ChatColor.RED + "Not enough money!";
    protected String plotNotForSale = ChatColor.RED + "Plot not for sale!";
    protected String userNotInFaction = ChatColor.RED + "User not in faction";
    protected String notAnInt = ChatColor.RED + "Not an integer (a number with no decimal)!";

    protected String success = ChatColor.GREEN + "Success!";

    protected SubCommand registerSubCommand(int length, Function<CommandInfo, Boolean> function, String... args) {
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

        return command;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        for(SubCommand subCommand : subCommands) {
            CommandInfo commandInfo = new CommandInfo();
            commandInfo.args = strings;
            commandInfo.executor = commandSender;

            if(subCommand.predicate.test(commandInfo)) {
                for(SubCommand.SecondaryPredicate second : subCommand.also) {
                    if(!((Predicate<String>) second.check).test(commandInfo.args[second.arg])) {
                        commandInfo.executor.sendMessage(second.message);
                        return false;
                    }
                }

                return subCommand.function.apply(commandInfo);
            }
        }
        commandSender.sendMessage(ChatColor.RED + "Not a real command!");
        return false;
    }
}
