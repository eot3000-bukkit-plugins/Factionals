package fly.factions.impl.dynmap;

import fly.factions.Factionals;
import fly.factions.api.model.Faction;
import fly.factions.api.model.LandAdministrator;
import fly.factions.api.model.Plot;
import fly.factions.api.model.Region;
import fly.factions.impl.util.Plots;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;

import java.util.*;

/**
 * Copyright of https://github.com/webbukkit
 *
 * https://github.com/webbukkit/Dynmap-Factions
 *
 * This code has been modified
 */

public class DynmapManager {
    private MarkerSet set;
    private MarkerSet regSet;
    private DynmapAPI api;

    public DynmapManager() {
        api = (DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap");

        set = api.getMarkerAPI().getMarkerSet("factions.factionals.dynmap");
        regSet = api.getMarkerAPI().getMarkerSet("regions.factionals.dynmap");

        if(set == null) {
            set = api.getMarkerAPI().createMarkerSet("factions.factionals.dynmap", "Factions", null, false);
        }
        if(regSet == null) {
            regSet = api.getMarkerAPI().createMarkerSet("regions.factionals.dynmap", "Regions", null, false);
        }

        set.setLayerPriority(100);
        regSet.setLayerPriority(0);

        Bukkit.getScheduler().runTaskTimer(Factionals.getFactionals(), this::run, 150, 150);
    }

    enum direction { XPLUS, ZPLUS, XMINUS, ZMINUS }

    private void run() {
        for(AreaMarker marker : set.getAreaMarkers()) {
            marker.deleteMarker();
        }
        for(AreaMarker marker : regSet.getAreaMarkers()) {
            marker.deleteMarker();
        }

        for(Faction faction : Factionals.getFactionals().getRegistry(Faction.class).list()) {
            for(World world : Bukkit.getWorlds()) {
                addToMap(set, faction, world, false);

                for(Region region : faction.getRegions()) {
                    addToMap(regSet, region, world, false);
                }

                addToMap(regSet, faction, world, true);
            }
        }
    }

    private int floodFillTarget(TileFlags src, TileFlags dest, int x, int y) {
        int cnt = 0;
        ArrayDeque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[] { x, y });

        while(!stack.isEmpty()) {
            int[] nxt = stack.pop();
            x = nxt[0];
            y = nxt[1];
            if(src.getFlag(x, y)) { /* Set in src */
                src.setFlag(x, y, false);   /* Clear source */
                dest.setFlag(x, y, true);   /* Set in destination */
                cnt++;
                if(src.getFlag(x+1, y))
                    stack.push(new int[] { x+1, y });
                if(src.getFlag(x-1, y))
                    stack.push(new int[] { x-1, y });
                if(src.getFlag(x, y+1))
                    stack.push(new int[] { x, y+1 });
                if(src.getFlag(x, y-1))
                    stack.push(new int[] { x, y-1 });
            }
        }
        return cnt;
    }

    private void addStyle(LandAdministrator admin, AreaMarker m) {
        m.setLineStyle(1, admin.getBorderOpacity(), admin.getBorderColor().asRGB());
        m.setFillStyle(admin.getFillOpacity(), admin.getFillColor().asRGB());
    }

    private void addToMap(MarkerSet set, LandAdministrator admin, World world, boolean outline) {
        double[] x;
        double[] z;
        int poly_index = 0; /* Index of polygon for given faction */

        Collection<Plot> blocks = admin.getPlots();



        LinkedList<Plot> nodevals = new LinkedList<>();
        TileFlags curblks = new TileFlags();
        /* Loop through blocks: set flags on blockmaps */
        for (Plot b : blocks) {
            if(Plots.getW(b.getLocationId()) == Plots.getWorldId(world)) {
                curblks.setFlag(Plots.getX(b.getLocationId()), Plots.getZ(b.getLocationId()), true); /* Set flag for block */
                nodevals.addLast(b);
            }
        }

        /* Loop through until we don't find more areas */
        while (nodevals != null) {
            LinkedList<Plot> ournodes = null;
            LinkedList<Plot> newlist = null;
            TileFlags ourblks = null;
            int minx = Integer.MAX_VALUE;
            int minz = Integer.MAX_VALUE;
            for (Plot node : nodevals) {
                int nodex = Plots.getX(node.getLocationId());
                int nodez = Plots.getZ(node.getLocationId());
                /* If we need to start shape, and this block is not part of one yet */
                if ((ourblks == null) && curblks.getFlag(nodex, nodez)) {
                    ourblks = new TileFlags();  /* Create map for shape */
                    ournodes = new LinkedList<>();
                    floodFillTarget(curblks, ourblks, nodex, nodez);   /* Copy shape */
                    ournodes.add(node); /* Add it to our node list */
                    minx = nodex;
                    minz = nodez;
                }
                /* If shape found, and we're in it, add to our node list */
                else if ((ourblks != null) && ourblks.getFlag(nodex, nodez)) {
                    ournodes.add(node);
                    if (nodex < minx) {
                        minx = nodex;
                        minz = nodez;
                    } else if ((nodex == minx) && (nodez < minz)) {
                        minz = nodez;
                    }
                } else {  /* Else, keep it in the list for the next polygon */
                    if (newlist == null) newlist = new LinkedList<>();
                    newlist.add(node);
                }
            }
            nodevals = newlist; /* Replace list (null if no more to process) */
            if (ourblks != null) {
                /* Trace outline of blocks - start from minx, minz going to x+ */
                int init_x = minx;
                int init_z = minz;
                int cur_x = minx;
                int cur_z = minz;
                direction dir = direction.XPLUS;
                ArrayList<int[]> linelist = new ArrayList<>();
                linelist.add(new int[]{init_x, init_z}); // Add start point
                while ((cur_x != init_x) || (cur_z != init_z) || (dir != direction.ZMINUS)) {
                    switch (dir) {
                        case XPLUS: /* Segment in X+ direction */
                            if (!ourblks.getFlag(cur_x + 1, cur_z)) { /* Right turn? */
                                linelist.add(new int[]{cur_x + 1, cur_z}); /* Finish line */
                                dir = direction.ZPLUS;  /* Change direction */
                            } else if (!ourblks.getFlag(cur_x + 1, cur_z - 1)) {  /* Straight? */
                                cur_x++;
                            } else {  /* Left turn */
                                linelist.add(new int[]{cur_x + 1, cur_z}); /* Finish line */
                                dir = direction.ZMINUS;
                                cur_x++;
                                cur_z--;
                            }
                            break;
                        case ZPLUS: /* Segment in Z+ direction */
                            if (!ourblks.getFlag(cur_x, cur_z + 1)) { /* Right turn? */
                                linelist.add(new int[]{cur_x + 1, cur_z + 1}); /* Finish line */
                                dir = direction.XMINUS;  /* Change direction */
                            } else if (!ourblks.getFlag(cur_x + 1, cur_z + 1)) {  /* Straight? */
                                cur_z++;
                            } else {  /* Left turn */
                                linelist.add(new int[]{cur_x + 1, cur_z + 1}); /* Finish line */
                                dir = direction.XPLUS;
                                cur_x++;
                                cur_z++;
                            }
                            break;
                        case XMINUS: /* Segment in X- direction */
                            if (!ourblks.getFlag(cur_x - 1, cur_z)) { /* Right turn? */
                                linelist.add(new int[]{cur_x, cur_z + 1}); /* Finish line */
                                dir = direction.ZMINUS;  /* Change direction */
                            } else if (!ourblks.getFlag(cur_x - 1, cur_z + 1)) {  /* Straight? */
                                cur_x--;
                            } else {  /* Left turn */
                                linelist.add(new int[]{cur_x, cur_z + 1}); /* Finish line */
                                dir = direction.ZPLUS;
                                cur_x--;
                                cur_z++;
                            }
                            break;
                        case ZMINUS: /* Segment in Z- direction */
                            if (!ourblks.getFlag(cur_x, cur_z - 1)) { /* Right turn? */
                                linelist.add(new int[]{cur_x, cur_z}); /* Finish line */
                                dir = direction.XPLUS;  /* Change direction */
                            } else if (!ourblks.getFlag(cur_x - 1, cur_z - 1)) {  /* Straight? */
                                cur_z--;
                            } else {  /* Left turn */
                                linelist.add(new int[]{cur_x, cur_z}); /* Finish line */
                                dir = direction.XMINUS;
                                cur_x--;
                                cur_z--;
                            }
                            break;
                    }
                }
                /* Build information for specific area */
                String polyid = admin.getClass().getName() + "__" + admin.getId() + "__" + world + "__" + poly_index + "__" + outline;
                int sz = linelist.size();
                x = new double[sz];
                z = new double[sz];
                for (int i = 0; i < sz; i++) {
                    int[] line = linelist.get(i);
                    x[i] = (double) line[0] * 16.0;
                    z[i] = (double) line[1] * 16.0;
                }

                AreaMarker m = set.createAreaMarker(polyid, admin.getDesc(), false, world.getName(), x, z, false);

                if(outline) {
                    m.setFillStyle(0, 0);
                    m.setLineStyle(3, 1, admin.getBorderColor().asRGB());
                } else {
                    /* Set line and fill properties */
                    addStyle(admin, m);
                }



                /* Add to map */
                poly_index++;
            }
        }
    }
}
