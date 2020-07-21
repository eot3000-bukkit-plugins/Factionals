package fly.factions.commands;

import com.google.common.collect.Iterables;
import fly.factions.model.*;
import fly.factions.permissions.GroupPermission;
import fly.factions.villagers.Village;
import fly.factions.villagers.structures.Structure;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionCommand extends CompanyCommand {
    public FactionCommand() {
        super(true);

        registerSubCommand(2, this::createGroup, "create")
                .also(2, nameTaken, pGroupNull);

        registerSubCommand(4, this::addMember, "member", "add")
                .also(3, notAUser, pPlayerNotNull)
                .also(4, notAFaction, pGroupFaction)
                .groupPermission(4, GroupPermission.MEMBER_INVITE);

        registerSubCommand(4, this::removeMember, "member", "remove")
                .also(3, notAUser, pPlayerNotNull)
                .also(4, notAFaction, pGroupFaction)
                .groupPermission(4, GroupPermission.MEMBER_KICK);

        registerSubCommand(5, this::rankCreate, "rank", "create")
                .also(3, notAFaction, pGroupFaction)
                .also(5, notAUser, pPlayerNotNull)
                .groupPermission(3, GroupPermission.RANK_EDIT);

        registerSubCommand(6, this::rankPermissionAdd, "rank", "permission", "add")
                .also(4, notAFaction, pGroupNotNull)
                .also(6, notAPermission, pFactionPermission)
                .groupPermission(4, GroupPermission.RANK_EDIT);

        registerSubCommand(6, this::rankPermissionRemove, "rank", "permission", "remove")
                .also(4, notAFaction, pGroupNotNull)
                .also(6, notAPermission, pFactionPermission)
                .groupPermission(4, GroupPermission.RANK_EDIT);

        registerSubCommand(6, this::rankPlayerAdd, "rank", "player", "add")
                .also(4, notAFaction, pGroupFaction)
                .also(6, notAUser, pPlayerNotNull);

        registerSubCommand(6, this::rankPlayerRemove, "rank", "player", "remove")
                .also(4, notAFaction, pGroupFaction)
                .also(6, notAUser, pPlayerNotNull);

        registerSubCommand(2, this::factionClaim, "claim")
                .also(2, notAFaction, pGroupFaction)
                .groupPermission(2, GroupPermission.LAND_CLAIM);

        //registerSubCommand(2, this::faction123, "village", "claim");
        //registerSubCommand(2, this::faction12, "village", "info");

        registerSubCommand(1, this::factionUnclaim, "unclaim");

        registerSubCommand(1, this::factionMap, "map");
    }

    private boolean faction123(CommandInfo info) {
        Village village = new Village();

        village.addPlot(factionals.getPlotByLocation(new PlotLocation(((Player) info.executor).getLocation())));

        ((Faction)factionals.getGroupByName("hi")).addVillage(village);

        return true;
    }

    private boolean faction12(CommandInfo info) {
        Village village = Iterables.get(() -> ((Faction) factionals.getGroupByName("hi")).getVillages().iterator(), 0);

        for(Structure structure : village.getStructures()) {
            System.out.println(structure.getDoor());
        }
        for(Plot plot : village.getPlots()) {
            System.out.println(plot.getLocation().toString());
        }

        return true;
    }

    protected boolean addMember(CommandInfo info) {
        factionals.getGroupByName(info.args[3]).addMember(factionals.getUserByName(info.args[2]));
        info.executor.sendMessage(success);
        return true;
    }

    protected boolean removeMember(CommandInfo info) {
        factionals.getGroupByName(info.args[3]).removeMember(factionals.getUserByName(info.args[2]));
        info.executor.sendMessage(success);
        return true;
    }

    @Override
    protected boolean createGroup(CommandInfo info) {
        factionals.addGroup(new Faction(factionals.getUserByUUID(((Player)info.executor).getUniqueId()), info.args[1]));
        info.executor.sendMessage(success);
        return true;
    }

    protected boolean factionClaim(CommandInfo info) {
        Player p = (Player) info.executor;
        Faction f = (Faction) factionals.getGroupByName(info.args[1]);

        Plot plot = new Plot(f, f, new PlotLocation(p.getLocation()));

        f.claim(plot);

        info.executor.sendMessage(success);
        return true;
    }

    protected boolean factionUnclaim(CommandInfo info) {
        Player p = (Player) info.executor;
        Faction f = (Faction) factionals.getGroupByName(info.args[1]);
        Plot plot = factionals.getPlotByLocation(new PlotLocation(p.getLocation()));

        if(plot == null) {
            p.sendMessage(notAPlot);
            return false;
        }

        if(!plot.getFaction().hasPermission(GroupPermission.LAND_UNCLAIM, factionals.getUserByUUID(p.getUniqueId()))) {
            p.sendMessage(noPermission);
            return false;
        }

        f.unclaim(plot.getLocation());

        info.executor.sendMessage(success);
        return true;
    }

    private boolean factionMap(CommandInfo info) {
        List<String> map = new ArrayList<>();
        Map<Faction, String> symbols = new HashMap<>();
        Player player = (Player) info.executor;
        Chunk chunk = player.getLocation().getChunk();

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        World world = chunk.getWorld();

        String first = ChatColor.DARK_GRAY.toString();

        for(int i = -20; i <= 20; i++) {
            first+="_";
        }

        map.add(first);

        for(int z = -5; z <= 5; z++) {
            String current = "";
            int countX = -17;

            switch(z) {
                case (-5): {
                    current = compass(player, 0) + compass(player, 1) + compass(player, 2);
                    break;
                }

                case (-4): {
                    current = compass(player, 3) + compass(player, 4) + compass(player, 5);
                    break;
                }

                case (-3): {
                    current = compass(player, 6) + compass(player, 7) + compass(player, 8);
                    break;
                }

                default: {
                    countX = -20;
                    break;
                }
            }

            for(int x = countX; x <= 20; x++) {
                Plot plot = factionals.getPlotByLocation(new PlotLocation(chunkX+x, chunkZ+z, world));

                if(x == z && x == 0) {
                    current+=ChatColor.BLACK + "X";
                    continue;
                }

                if(plot == null) {
                    current+=ChatColor.DARK_GREEN + "-";
                    continue;
                }

                while(!symbols.containsKey(plot.getFaction())) {
                    String rand = randomSymbol();

                    if(!symbols.containsValue(rand)) {
                        symbols.put(plot.getFaction(), rand);
                    }
                }

                current+=symbols.get(plot.getFaction());
            }

            map.add(current);
        }
        for(String line : map) {
            info.executor.sendMessage(line);
        }

        return true;
    }

    private String randomSymbol() {
        List<ChatColor> colors = Arrays.asList(ChatColor.DARK_RED, ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.AQUA, ChatColor.BLUE, ChatColor.DARK_PURPLE);
        List<Character> characters = Arrays.asList('#', '+', '$', '%', '^', '&', '*', '/', '\\', '=');

        return colors.get(random.nextInt(colors.size())).toString() + characters.get(random.nextInt(characters.size()));
    }

    private String compass(Player player, int comp) {
        Map<Integer, Character> chars = new HashMap<>();
        Map<BlockFace, Integer> pos = new HashMap<>();

        chars.put(0, '\\');
        chars.put(1, 'N');
        chars.put(2, '/');
        chars.put(3, 'W');
        chars.put(4, '#');
        chars.put(5, 'E');
        chars.put(6, '/');
        chars.put(7, 'S');
        chars.put(8, '\\');

        pos.put(BlockFace.NORTH_WEST, 0);
        pos.put(BlockFace.NORTH, 1);
        pos.put(BlockFace.NORTH_EAST, 2);
        pos.put(BlockFace.WEST, 3);
        pos.put(BlockFace.UP, 4);
        pos.put(BlockFace.DOWN, 4);
        pos.put(BlockFace.EAST, 5);
        pos.put(BlockFace.SOUTH_WEST, 6);
        pos.put(BlockFace.SOUTH, 7);
        pos.put(BlockFace.SOUTH_EAST, 8);

        return (pos.get(player.getFacing()) == comp ? ChatColor.RED.toString() : ChatColor.GOLD.toString()) + chars.get(comp);
    }
}
