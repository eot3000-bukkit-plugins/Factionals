package fly.factions.commands;

import java.util.function.Function;
import java.util.function.Predicate;

public class SubCommand {
    public Predicate<CommandInfo> predicate;
    public Function<CommandInfo, Boolean> function;
}
