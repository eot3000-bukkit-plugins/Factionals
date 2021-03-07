package fly.factions.impl.listeners;

/*import fly.factions.Factionals;
import fly.factions.api.model.Faction;
import fly.factions.api.model.Plot;
import fly.factions.api.model.User;
import fly.factions.api.permissions.FactionPermission;
import fly.factions.api.registries.Registry;
import fly.factions.impl.util.Plots;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

public class ChatListener extends ListenerImpl {
    @EventHandler
    public void onChatUse(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        User user = getUserFromPlayer(event.getPlayer());
        String message = event.getMessage();

        if(user.claimMode() != 0) {
            if(message.startsWith("c ")) {
                if(user.faction() != null && user.faction().hasPermission(user, FactionPermission.TERRITORY)) {

                    return;
                }
            }
            if(message.startsWith("map ")) {
                try {
                    Faction userFaction = getUserFromPlayer(event.getPlayer()).faction();

                    List<Character> characters = new ArrayList<>(Arrays.asList('#', '&', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'));
                    Map<Faction, Character> factionCharacters = new HashMap<>();

                    String[] split = message.split(" ");

                    int height = Integer.parseInt(split[1])*2+1;
                    int width = Integer.parseInt(split[2])*2+1;

                    int xb = event.getPlayer().getLocation().getChunk().getX();
                    int zb = event.getPlayer().getLocation().getChunk().getZ();
                    World w = event.getPlayer().getLocation().getWorld();

                    int xm = (int) Math.floor(height/2);
                    int zm = (int) Math.floor(width/2);

                    List<String> ret = new ArrayList<>();

                    factionCharacters.put(null, '-');

                    for(int z = 0; z < height; z++) {
                        String line = "";

                        for(int x = 0; x < width; x++) {
                            int plotId = Plots.getLocationId((xb+x)-xm, (zb+z)-zm, w);
                            Faction faction = ((Registry<Plot, Integer>) Factionals.getFactionals().getRegistry(Plot.class)).get(plotId).faction();
                            String chunkAddition;

                            if(faction == null || faction.isDeleted()) {
                                faction = null;
                            }

                            if(xm == x && zm == z) {
                                chunkAddition = ChatColor.BLACK + "";
                            } else if(faction == null) {
                                chunkAddition = ChatColor.GRAY + "";
                            } else if(faction.equals(userFaction)) {
                                chunkAddition = ChatColor.GREEN + "";
                            } else {
                                chunkAddition = ChatColor.DARK_GRAY + "";
                            }

                            if(faction != null && !factionCharacters.containsKey(faction)) {
                                factionCharacters.put(faction, characters.get(0));
                                characters.remove(0);
                            }

                            chunkAddition+=factionCharacters.get(faction);

                            line+=chunkAddition;
                        }
                        ret.add(line);
                    }

                    for(String string : ret) {
                        event.getPlayer().sendMessage(string);
                    }

                    return;
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    //
                }
            }
        }

        event.setCancelled(false);
    }
}
//j*/