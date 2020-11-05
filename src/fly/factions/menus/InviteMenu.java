package fly.factions.menus;

import fly.factions.Factionals;
import fly.factions.messages.Messages;
import fly.factions.model.Faction;
import fly.factions.model.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class InviteMenu extends Menu {
    @Override
    public void runButtonClick(InventoryClickEvent event) {
        if(event.getCurrentItem().getType().equals(Material.REDSTONE_BLOCK)) {
            event.setCancelled(true);
            return;
        }

        PersistentDataContainer cont = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
        User victim = Factionals.getFactionals().getUserFromUUID(UUID.fromString(cont.get(UUID_NAMESPACE, PersistentDataType.STRING)));
        Player player = Bukkit.getPlayer(victim.getUuid());

        User inviter = Factionals.getFactionals().getUserFromPlayer((Player) event.getWhoClicked());

        player.sendMessage(Messages.INVITATION_TO_FACTION.replaceAll("%1", inviter.getFaction().getName()));

        inviter.getFaction().tellEveryone(Messages.FACTION_MESSAGE_MEMBER_INVITE.replaceAll("%1", victim.getName()));

        victim.flagChat((s, p) -> {
            if(s.equalsIgnoreCase("Join Faction")) {
                if(victim.getFaction() != null) {
                    victim.getFaction().removeMember(victim);
                }
                victim.setFaction(inviter.getFaction());
                inviter.getFaction().addMember(victim);

                inviter.getFaction().tellEveryone(Messages.FACTION_MESSAGE_MEMBER_JOIN.replaceAll("%1", victim.getName()));
            }
            victim.flagChat(User.defaultFlag);
        });
    }

    @Override
    public Inventory createInventory(Player player, String info) {
        Inventory inventory = Bukkit.createInventory(player, 27, ChatColor.translateAlternateColorCodes('&', "&2Invite Members"));
        Faction faction = Factionals.getFactionals().getUserFromPlayer(player).getFaction();

        if(faction == null) {
            ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(Messages.NOT_MEMBER_FACTION);
            itemMeta.getPersistentDataContainer().set(MENU_NAMESPACE, PersistentDataType.STRING, "invite-members");

            itemStack.setItemMeta(itemMeta);

            for(int x = 0; x < 27; x++) {
                inventory.setItem(x, itemStack);
            }
        } else {
            if (!faction.getLeader().equals(Factionals.getFactionals().getUserFromPlayer(player))) {
                ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK);
                ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Messages.NO_PERMISSION));
                itemMeta.getPersistentDataContainer().set(MENU_NAMESPACE, PersistentDataType.STRING, "invite-members");

                itemStack.setItemMeta(itemMeta);

                for(int x = 0; x < 27; x++) {
                    inventory.setItem(x, itemStack);
                }
            } else {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if(!online.equals(player)) {
                        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                        ItemMeta itemMeta = itemStack.getItemMeta();

                        ((SkullMeta) itemMeta).setOwningPlayer(online);
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2" + online.getName()));
                        itemMeta.getPersistentDataContainer().set(MENU_NAMESPACE, PersistentDataType.STRING, "invite-members");
                        itemMeta.getPersistentDataContainer().set(UUID_NAMESPACE, PersistentDataType.STRING, online.getUniqueId().toString());

                        itemStack.setItemMeta(itemMeta);

                        inventory.addItem(itemStack);
                    }
                }
            }
        }

        return inventory;
    }
}
