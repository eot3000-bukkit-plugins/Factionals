package fly.factions.impl.model;

import fly.factions.Factionals;
import fly.factions.api.model.Faction;
import fly.factions.api.model.User;
import fly.factions.api.registries.Registry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class UserImpl implements User {
    private UUID uuid;
    private String name;
    private Faction faction;

    private int claimMode;
    //private OpenedMenu menu;

    private Set<Faction> invites = new HashSet<>();

    public UserImpl(UUID uuid) {
        this(uuid, Bukkit.getOfflinePlayer(uuid).getName());
    }

    public UserImpl(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    private Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public void sendMessage(String s) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if(player.isOnline()) {
            ((Player) player).sendMessage(s);
        }
    }

    @Override
    public double getMoney() {
        return 0;
    }

    @Override
    public void setMoney(double x) {

    }

    @Override
    public void addMoney(double x) {

    }

    @Override
    public void takeMoney(double x) {

    }

    @Override
    public String getId() {
        return uuid.toString();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));

        item.setItemMeta(meta);

        return item;
    }

    /*@Override
    public OpenedMenu getMenu() {
        return menu;
    }

    @Override
    public void setMenu(OpenedMenu menu) {
        this.menu = menu;

        if(menu != null) {
            Inventory inventory = Bukkit.createInventory(getPlayer(), menu.inventorySize(), menu.title());

            for (int x = 0; x <= menu.size(); x++) {
                ItemStack item = menu.getButton(x).getItem();

                if(item != null) {
                    inventory.setItem(x, item);
                }
            }

            Bukkit.getScheduler().runTaskLater(Factionals.getFactionals(), () -> {
                getPlayer().openInventory(inventory);
                this.menu = menu;
            }, 1);
        }
    }*/

    @Override
    public Faction getFaction() {
        return faction;
    }

    @Override
    public void setFaction(Faction faction) {
        if(this.faction != null) {
            this.faction.removeMember(this);
        }

        this.faction = faction;

        faction.addMember(this);
    }

    @Override
    public Collection<Faction> getInvites() {
        return new ArrayList<>(invites);
    }

    @Override
    public void addInvite(Faction faction) {
        invites.add(faction);
    }

    @Override
    public void removeInvite(Faction faction) {
        invites.remove(faction);
    }

    /*@Override
    public int claimMode() {
        return claimMode;
    }*/
}
