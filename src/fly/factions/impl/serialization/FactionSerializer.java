package fly.factions.impl.serialization;

import fly.factions.Factionals;
import fly.factions.api.model.*;
import fly.factions.api.permissions.Permissibles;
import fly.factions.api.permissions.PlotPermission;
import fly.factions.api.registries.Registry;
import fly.factions.api.serialization.Serializer;
import fly.factions.impl.model.*;
import fly.factions.impl.util.Plots;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FactionSerializer extends Serializer<Faction> {
    private File dir = new File("plugins\\Factionals\\factions");

    private boolean plots = false;

    public FactionSerializer(Factionals factionals) {
        super(Faction.class, factionals);
    }

    @Override
    public void onLoad() {
        this.plots = true;
    }

    @Override
    public File dir() {
        return dir;
    }

    @Override
    public Faction load(File file) {
        Registry<User, UUID> r = factionals.getRegistry(User.class, UUID.class);

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        if(!configuration.getBoolean("deleted")) {
            Faction faction;

            if(!plots) {
                faction = new FactionImpl(configuration.getString("name"), r.get(UUID.fromString(configuration.getString("leader"))), configuration.getLong("creationTime"));

                faction.setBorderColor(Color.fromRGB(configuration.getInt("br"), configuration.getInt("bg"), configuration.getInt("bb")));
                faction.setFillColor(Color.fromRGB(configuration.getInt("fr"), configuration.getInt("fg"), configuration.getInt("fb")));

                faction.setFillOpacity(configuration.getDouble("fo"));

                //Departments

                ConfigurationSection departments = configuration.getConfigurationSection("departments");

                for (String string : departments.getKeys(false)) {
                    ConfigurationSection department = departments.getConfigurationSection(string);
                    ExecutiveDivision division = new ExecutiveDivisionImpl(department.getString("name"), r.get(UUID.fromString(department.getString("leader"))), faction);

                    for (String member : department.getStringList("members")) {
                        division.addMember(r.get(UUID.fromString(member)));
                    }

                    faction.addDepartment(division);
                }

                //Regions

                ConfigurationSection regions = configuration.getConfigurationSection("regions");

                for (String string : regions.getKeys(false)) {
                    ConfigurationSection region = regions.getConfigurationSection(string);
                    Region factionRegion = new RegionImpl(region.getString("name"), r.get(UUID.fromString(region.getString("leader"))), faction);

                    for (String member : region.getStringList("members")) {
                        factionRegion.addMember(r.get(UUID.fromString(member)));
                    }

                    factionRegion.setBorderColor(Color.fromRGB(region.getInt("br"), region.getInt("bg"), region.getInt("bb")));
                    factionRegion.setFillColor(Color.fromRGB(region.getInt("fr"), region.getInt("fg"), region.getInt("fb")));

                    factionRegion.setFillOpacity(region.getDouble("fo"));

                    //Lots

                    ConfigurationSection lots = region.getConfigurationSection("lots");

                    for(String lotString : lots.getKeys(false)) {
                        Lot factionLot = new LotImpl(factionRegion, Integer.parseInt(lotString), Plots.getWorld(lots.getConfigurationSection(lotString).getInt("world")));

                        factionRegion.setLot(factionLot.getId(), factionLot);
                    }

                    //Towns

                    ConfigurationSection towns = region.getConfigurationSection("towns");

                    for(String townString : towns.getKeys(false)) {
                        ConfigurationSection town = towns.getConfigurationSection(townString);

                        Town factionTown = new TownImpl(town.getString("name"), r.get(UUID.fromString(town.getString("leader"))), factionRegion);

                        //Members

                        for (String member : town.getStringList("members")) {
                            factionTown.addMember(r.get(UUID.fromString(member)));
                        }

                        factionRegion.addTown(factionTown);
                    }

                    faction.addRegion(factionRegion);
                }

                //Members

                for (String member : configuration.getStringList("members")) {
                    r.get(UUID.fromString(member)).setFaction(faction);
                }

                //Plots

                ConfigurationSection plots = configuration.getConfigurationSection("plots");

                for (String string : plots.getKeys(false)) {
                    ConfigurationSection plot = plots.getConfigurationSection(string);
                    Plot factionPlot = new PlotImpl(plot.getInt("x"), plot.getInt("z"), Plots.getWorld(plot.getInt("w")), faction);


                    factionPlot.setAdministrator((LandAdministrator) getPlotOwner(plot.getString("administrator")));
                    /*
                    factionPlot.setPrice(plot.getInt("price"));
                    factionPlot.setOwner(getPlotOwner(plot.getString("owner")));

                    ConfigurationSection plotPermissions = plot.getConfigurationSection("permissions");

                    for (String key : plotPermissions.getKeys(false)) {
                        for (String permissible : plotPermissions.getStringList(key)) {
                            Permissible plotPermissible = Permissibles.get(permissible).get(0);
                            PlotPermission permission = PlotPermission.valueOf(key);

                            factionPlot.setPermission(plotPermissible, permission, true);
                        }
                    }*/
                }
            } else {
                faction = factionals.getRegistry(Faction.class, String.class).get(configuration.getString("name"));

                for(Region region : faction.getRegions()) {
                    ConfigurationSection regionConfig = configuration.getConfigurationSection("regions." + region.getName() + ".lots");

                    for(Lot lot : region.getLots().values()) {
                        ConfigurationSection lotSection = regionConfig.getConfigurationSection("" + lot.getId());

                        World world = Plots.getWorld(lotSection.getInt("world"));

                        List<String> lotPlaces = lotSection.getStringList("areas");

                        for(String place : lotPlaces) {
                            String[] split = place.split(",");

                            int x = Integer.parseInt(split[0]);
                            int z = Integer.parseInt(split[1]);

                            Location location = new Location(world, x, 0, z);

                            Plot plot = factionals.getRegistry(Plot.class, Integer.class).get(Plots.getLocationId(location));

                            plot.setLot(location, lot);
                        }

                        lot.setPrice(lotSection.getInt("price"));
                        lot.setOwner(getPlotOwner(lotSection.getString("owner")));

                        lot.setTown(region.getTown(lotSection.getString("town")));

                        ConfigurationSection lotPermissions = lotSection.getConfigurationSection("permissions");

                        for (String key : lotPermissions.getKeys(false)) {
                            for (String permissible : lotPermissions.getStringList(key)) {
                                Permissible plotPermissible = Permissibles.get(permissible).get(0);
                                PlotPermission permission = PlotPermission.valueOf(key);

                                lot.setPermission(plotPermissible, permission, true);
                            }
                        }
                    }
                }
            }

            return faction;
        }

        return null;
    }

    private PlotOwner getPlotOwner(String owner) {
        List<Permissible> permissibles = Permissibles.get(owner);

        if(owner.isEmpty()) {
            return null;
        }

        for(Permissible permissible : permissibles) {
            if(permissible instanceof PlotOwner) {
                return (PlotOwner) permissible;
            }
        }

        return null;
    }

    @Override
    public void save(Faction faction) {
        File file = new File("plugins\\Factionals\\factions\\" + faction.getCreationTime() + "-" + faction.getName());

        YamlConfiguration configuration = new YamlConfiguration();

        Map<String, Map<String, Object>> departments = new HashMap<>();
        Map<String, Map<String, Object>> regions = new HashMap<>();
        Map<Integer, Map<String, Object>> plots = new HashMap<>();
        List<String> members = new ArrayList<>();

        //Departments

        for(ExecutiveDivision division : faction.getDepartments()) {
            Map<String, Object> department = new HashMap<>();

            List<String> departmentMembers = new ArrayList<>();

            for(User user : division.getMembers()) {
                departmentMembers.add(user.getUniqueId().toString());
            }

            department.put("members", departmentMembers);
            department.put("name", division.getName());
            department.put("leader", division.getLeader().getUniqueId().toString());

            departments.put(division.getName(), department);
        }

        //Regions

        for(Region region : faction.getRegions()) {
            Map<String, Object> factionRegion = new HashMap<>();

            List<String> regionMembers = new ArrayList<>();
            Map<String, Object> regionTowns = new HashMap<>();
            Map<String, Object> regionLots = new HashMap<>();

            //TODO: fix
            Map<Integer, List<Pair<Integer, Integer>>> lotsAreas = new HashMap<>();

            for(Integer lot : region.getLots().keySet()) {
                lotsAreas.put(lot, new ArrayList<>());
            }

            for(Plot plot : region.getPlots()) {
                Map<Pair<Integer, Integer>, Integer> map = plot.getLocations();

                for (Pair<Integer, Integer> area : map.keySet()) {
                    lotsAreas.get(map.get(area)).add(area);
                }
            }

            for(User user : region.getMembers()) {
                regionMembers.add(user.getUniqueId().toString());
            }

            factionRegion.put("members", regionMembers);
            factionRegion.put("name", region.getName());
            factionRegion.put("leader", region.getLeader().getUniqueId().toString());

            factionRegion.put("br", region.getBorderColor().getRed());
            factionRegion.put("bg", region.getBorderColor().getGreen());
            factionRegion.put("bb", region.getBorderColor().getBlue());
            factionRegion.put("bo", region.getBorderOpacity());

            factionRegion.put("fr", region.getFillColor().getRed());
            factionRegion.put("fg", region.getFillColor().getGreen());
            factionRegion.put("fb", region.getFillColor().getBlue());
            factionRegion.put("fo", region.getFillOpacity());

            for(Town town : region.getTowns()) {
                Map<String, Object> factionTown = new HashMap<>();
                List<String> townMembers = new ArrayList<>();

                factionTown.put("name", town.getName());
                factionTown.put("leader", town.getLeader().getUniqueId().toString());

                for(User member : town.getMembers()) {
                    townMembers.add(member.getUniqueId().toString());
                }

                factionTown.put("members", townMembers);

                regionTowns.put(town.getName(), factionTown);
            }

            for(Lot lot : region.getLots().values()) {
                Map<String, Object> factionLot = new HashMap<>();

                String ownerId = lot.getOwner() == null ? "" : lot.getOwner().getId();

                factionLot.put("owner", ownerId);
                factionLot.put("price", lot.getPrice());
                factionLot.put("world", Plots.getWorldId(lot.getWorld()));

                Map<String, Object> permissions = new HashMap<>();
                List<String> areas = new ArrayList<>();

                for(PlotPermission permission : PlotPermission.values()) {
                    List<String> specPerm = new ArrayList<>();

                    for(Permissible permissible : lot.getPermissions().get(permission)) {
                        specPerm.add(permissible.getId());
                    }

                    permissions.put(permission.name(), specPerm);
                }

                for(Pair<Integer, Integer> area : lotsAreas.get(lot.getId())) {
                    areas.add(area.getKey() + "," + area.getValue());
                }

                factionLot.put("permissions", permissions);
                factionLot.put("areas", areas);
                factionLot.put("town", lot.getTown().getName());

                regionLots.put("" + lot.getId(), factionLot);
            }

            factionRegion.put("towns", regionTowns);
            factionRegion.put("lots", regionLots);

            regions.put(region.getName(), factionRegion);
        }

        //Plots

        for(Plot plot : faction.getPlots()) {
            Map<String, Object> factionPlot = new HashMap<>();

            factionPlot.put("administrator", plot.getAdministrator().getId());

            factionPlot.put("x", Plots.getX(plot.getLocationId()));
            factionPlot.put("z", Plots.getZ(plot.getLocationId()));
            factionPlot.put("w", Plots.getW(plot.getLocationId()));

            //Map<Pair<Integer, Integer>, Integer> map = plot.getLocations();

            /*factionPlot.put("owner", plot.getOwner().getId());
            factionPlot.put("price", plot.getPrice());

            Map<String, Object> permissions = new HashMap<>();

            for(PlotPermission permission : PlotPermission.values()) {
                List<String> specPerm = new ArrayList<>();

                for(Permissible permissible : plot.getPermissions().get(permission)) {
                    specPerm.add(permissible.getId());
                }

                permissions.put(permission.name(), specPerm);
            }

            factionPlot.put("permissions", permissions);*/

            plots.put(plot.getLocationId(), factionPlot);
        }

        //Members

        for(User user : faction.getMembers()) {
            members.add(user.getUniqueId().toString());
        }

        configuration.set("departments", departments);
        configuration.set("regions", regions);
        configuration.set("plots", plots);
        configuration.set("members", members);
        configuration.set("leader", faction.getLeader().getUniqueId().toString());
        configuration.set("name", faction.getName());
        configuration.set("deleted", faction.isDeleted());

        configuration.set("br", faction.getBorderColor().getRed());
        configuration.set("bg", faction.getBorderColor().getGreen());
        configuration.set("bb", faction.getBorderColor().getBlue());
        configuration.set("bo", faction.getBorderOpacity());

        configuration.set("fr", faction.getFillColor().getRed());
        configuration.set("fg", faction.getFillColor().getGreen());
        configuration.set("fb", faction.getFillColor().getBlue());
        configuration.set("fo", faction.getFillOpacity());

        try {
            configuration.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
