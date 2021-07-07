package fly.factions.api.commands;

import fly.factions.Factionals;
import fly.factions.api.model.Faction;
import fly.factions.api.model.User;
import fly.factions.api.permissions.FactionPermission;
import fly.factions.api.registries.Registry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.*;

import javafx.util.Pair;

public abstract class CommandDivision implements CommandExecutor {
    protected static Factionals API = Factionals.getFactionals();
    protected static Registry<User, UUID> USERS = API.getRegistry(User.class, UUID.class);
    protected static Registry<Faction, String> FACTIONS = API.getRegistry(Faction.class, String.class);

    private Map<String, CommandDivision> subCommands = new HashMap<>();
    private List<Pair<String, String>> helpEntries = new ArrayList<>();

    protected CommandDivision() {

    }

    protected CommandDivision(String command) {
        Bukkit.getPluginCommand(command).setExecutor(this);
    }

    protected final void addSubCommand(String command, CommandDivision division) {
        subCommands.put(command, division);
    }

    protected final void addHelpEntry(String syntax, String description) {
        helpEntries.add(new Pair<>(syntax, description));
    }

    protected final int constrain(int min, int max, int value) {
        return Math.min(max, Math.max(min,value));
    }

    protected final double constrain(double min, double max, double value) {
        return Math.min(max, Math.max(min,value));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        CommandDivision division = getNext(strings.length != 0 ? strings[0] : "");

        if(division == null) {
            help(commandSender, 0);

            return false;
        }

        if(division == this) {
            Class clazz = division.getClass();

            Method[] methods = clazz.getDeclaredMethods();

            for(Method method : methods) {
                if(method.getName().equalsIgnoreCase("run")) {
                    try {
                        method.invoke(division, pushIntoStart(strings, commandSender));
                    } catch (Exception e) {
                        e.printStackTrace();

                        int num = new Random().nextInt(2000);

                        commandSender.sendMessage(ChatColor.DARK_RED + "Fatal error at line " + num);
                        commandSender.sendMessage(ChatColor.DARK_RED + e.getClass().getName() + "@" + e.hashCode());
                        commandSender.sendMessage(ChatColor.DARK_RED + "Please contact the head of servers or administrators, Fly");

                        System.out.println("crashed server at time " + num);

                        return false;
                    }
                }
            }

            return false;
        }

        division.onCommand(commandSender, command, s, clean(strings));

        return false;
    }

    //TODO: deal with manual array copy

    private static String[] clean(String[] strings) {
        String[] ret = new String[strings.length-1];

        for(int i = 1; i < strings.length; i++) {
            ret[i-1] = strings[i];
        }

        return ret;
    }

    private static Object[] pushIntoStart(Object[] objects, Object insert) {
        Object[] ret = new Object[objects.length+1];

        for(int i = 0; i < objects.length; i++) {
            ret[i+1] = objects[i];
        }

        ret[0] = insert;

        return ret;
    }

    // If something, the sub command
    // If nothing but it terminates here, itself
    // If nothing and it doesn't terminate here, null
     public CommandDivision getNext(String string) {
        if(string.isEmpty()) {
            return null;
        }

        if(subCommands.containsKey(string)) {
            return subCommands.get(string);
        }
        if(subCommands.containsKey("*")) {
            return this;
        }

        return null;
    }

    public void help(CommandSender sender, int page) {
        for(Pair<String, String> command : helpEntries) {
            sender.sendMessage(ChatColor.DARK_AQUA + command.getKey() + ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + command.getValue());
        }
    }

    public enum ArgumentType {
        INT {
            @Override
            public boolean check(String string) {
                try {
                    Integer.parseInt(string);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            @Override
            public String format(String string) {
                return ChatColor.RED + "ERROR: " + ChatColor.YELLOW + string + ChatColor.RED + " needs to be an integer";
            }
        },
        LONG {
            @Override
            public boolean check(String string) {
                try {
                    Long.parseLong(string);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            @Override
            public String format(String string) {
                return ChatColor.RED + "ERROR: " + ChatColor.YELLOW + string + ChatColor.RED + " needs to be a long";
            }
        },
        DOUBLE {
            @Override
            public boolean check(String string) {
                try {
                    Double.parseDouble(string);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            @Override
            public String format(String string) {
                return ChatColor.RED + "ERROR: " + ChatColor.YELLOW + string + ChatColor.RED + " needs to be a double";
            }
        },

        STRING {
            @Override
            public boolean check(String string) {
                return true;
            }

            @Override
            public String format(String string) {
                return "";
            }
        },

        FACTION {
            @Override
            public boolean check(String string) {
                return FACTIONS.get(string) != null;
            }

            @Override
            public String format(String string) {
                return ChatColor.RED + "ERROR: the faction " + ChatColor.YELLOW + string + ChatColor.RED + " does not exist";
            }
        },
        NOT_FACTION {
            @Override
            public boolean check(String string) {
                return !FACTION.check(string);
            }

            @Override
            public String format(String string) {
                return ChatColor.RED + "ERROR: the faction " + ChatColor.YELLOW + string + ChatColor.RED + " already exists";
            }
        },
        USER {
            @Override
            public boolean check(String string) {
                return Bukkit.getPlayer(string) != null;
            }

            @Override
            public String format(String string) {
                return ChatColor.RED + "ERROR: the user " + ChatColor.YELLOW + string + ChatColor.RED + " does not exist";
            }
        },
        @SuppressWarnings("all")
        FACTION_PERMISSION {
            @Override
            public boolean check(String string) {
                try {
                    return FactionPermission.valueOf(string) != null;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public String format(String string) {
                return ChatColor.RED + "ERROR: the permission " + ChatColor.YELLOW + string + ChatColor.RED + " does not exist";
            }
        };

        public static boolean checkAll(CommandSender sender, String[] strings, ArgumentType... argumentTypes) {
            int count = 0;

            for(String string : strings) {
                if(!argumentTypes[count].check(string)) {
                    sender.sendMessage(argumentTypes[count].format(string));

                    return false;
                }

                count++;
            }

            return true;
        }

        public abstract boolean check(String string);

        public abstract String format(String string);
    }
}
