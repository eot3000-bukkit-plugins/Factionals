package fly.factions.api.commands;

import fly.factions.Factionals;
import fly.factions.api.model.Faction;
import fly.factions.api.model.User;
import fly.factions.api.registries.Registry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class CommandRegister implements TabExecutor {
    private List<SubCommand> subCommands = new ArrayList<>();

    protected void addSubCommand(SubCommand command) {
        subCommands.add(command);
    }

    protected Method m(Class clazz, String string) {
        for(Method method : clazz.getMethods()) {
            if(method.getName().equalsIgnoreCase(string)) {
                return method;
            }
        }

        throw new RuntimeException("Error in the command register");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        a: for(SubCommand sc : subCommands) {
            if(strings.length != sc.parameters.size()) {
                continue;
            }

            List<Object> arguments = new ArrayList<>();

            for (int i = 0; i < strings.length; i++) {
                try {
                    Parameter parameter = sc.parameters.get(i);
                    Object o = parameter.function.apply(strings[i]);

                    if (o == null) {
                        if(parameter.type != String.class) {
                            commandSender.sendMessage(ChatColor.RED + "Command failed: please use a proper " + parameter.errorType + " at: " + ChatColor.YELLOW + strings[i]);
                            return false;
                        }
                        continue a;
                    }

                    arguments.add(o);
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(ChatColor.RED + "Command failed: please use a proper number at: " + ChatColor.YELLOW + strings[i]);
                    return false;
                }
            }
            Object invoke = null;

            if(sc.paramObject >= 0) {
                invoke = arguments.remove(sc.paramObject);
            }

            arguments.add(0, commandSender);

            try {
                boolean b = (boolean) sc.method.invoke(invoke, arguments.toArray());

                if(b) {
                    commandSender.sendMessage(ChatColor.GREEN + "Success!");
                }

                return b;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                commandSender.sendMessage(ChatColor.DARK_RED + "Error. Contact an admin");
                return false;
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();

                e.printStackTrace();

                if(cause instanceof ReturnNowException) {

                }
            }
        }

        commandSender.sendMessage(ChatColor.RED + "Command not found");
        return true;
    }

    public static void requirePlayer(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may run this command");
            throw new ReturnNowException();
        }
    }

    public static void requireMoreThan(int number, int check, String message, CommandSender sender) {
        if(number <= check) {
            sender.sendMessage(message);
            throw new CommandRegister.ReturnNowException();
        }
    }

    public static class NumberPotential {
        private int check;

        private Consumer<Integer> less;
        private Consumer<Integer> equal;
        private Consumer<Integer> more;

        public NumberPotential(int x) {
            this.check = x;

            less = (a) -> {};
            equal = (a) -> {};
            more = (a) -> {};
        }

        public NumberPotential less(Consumer<Integer> less) {
            this.less = less;

            return this;
        }

        public NumberPotential equal(Consumer<Integer> equal) {
            this.more = equal;

            return this;
        }

        public NumberPotential more(Consumer<Integer> more) {
            this.more = more;

            return this;
        }

        public void run(int x) {
            if(x < check) {
                less.accept(x);
            } else if(x == check) {
                equal.accept(x);
            } else {
                more.accept(x);
            }
        }
    }

    public static class SubCommand {
        private List<Parameter> parameters;
        private Method method;
        private int paramObject;

        private SubCommand(List<Parameter> parameters, Method method, int paramObject) {
            this.parameters = parameters;
            this.method = method;
            this.paramObject = paramObject;
        }

        public static class SubCommandBuilder {
            private List<Parameter> parameters;
            private Method method;
            private int paramObject;

            public SubCommandBuilder(Method method) {
                this.parameters = new ArrayList<>();
                this.method = method;
                this.paramObject = -1;
            }

            public SubCommandBuilder parameter(Parameter parameter) {
                parameters.add(parameter);

                return this;
            }

            public SubCommandBuilder paramObject(int paramObject) {
                this.paramObject = paramObject;

                return this;
            }

            public SubCommand build() {
                return new SubCommand(parameters, method, paramObject);
            }
        }
    }

    public static class Parameter<T> {
        private static Registry<Faction, String> fr = (Registry<Faction, String>) Factionals.getFactionals().getRegistry(Faction.class);
        private static Registry<User, UUID> ur = (Registry<User, UUID>) Factionals.getFactionals().getRegistry(User.class);

        public static final Parameter FACTION = new Parameter<>(Faction.class, fr::get, "faction name");
        public static final Parameter USER = new Parameter<>(User.class, (x) -> ur.get(Bukkit.getOfflinePlayer(x).getUniqueId()), "user name");
        public static final Parameter ONLINE_PLAYER = new Parameter<>(Player.class, Bukkit::getPlayer, "online player name");
        public static final Parameter STRING = new Parameter<>(String.class, String::toString, "");
        public static final Parameter INTEGER = new Parameter<>(Integer.class, Integer::parseInt, "number");
        public static final Parameter DOUBLE = new Parameter<>(Double.class, Double::parseDouble, "number");
        public static final Parameter BOOLEAN = new Parameter<>(Boolean.class, Boolean::parseBoolean, "boolean");

        private Class<T> type;
        private Function<String, T> function;
        private String errorType;

        public Parameter(Class<T> type, Function<String, T> function, String errorType) {
            this.type = type;
            this.function = function;
            this.errorType = errorType;
        }

        public Class<T> getType() {
            return type;
        }

        public T get(String string) {
            return function.apply(string);
        }

        public static Parameter<String> requireString(String s) {
            return new Parameter<>(String.class, (x) -> x.equalsIgnoreCase(s) ? s : null, "");
        }
    }


    public static class ReturnNowException extends RuntimeException {

    }
}
