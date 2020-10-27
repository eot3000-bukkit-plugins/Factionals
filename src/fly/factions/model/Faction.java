package fly.factions.model;
import fly.factions.Factionals;
import fly.factions.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Faction extends PlayerGroup {
    private static Factionals factionals = Factionals.getFactionals();

    public Faction(String name, User leader) {
        super(name, leader);

        members.add(leader);

        factionals.addFaction(this);
        leader.setFaction(this);
    }

    public static void startCreation(Player player, String s) {
        User user = factionals.getUserFromPlayer(player);


        user.flagChat(Faction::continueCreation);

        Bukkit.getScheduler().runTaskLater(factionals, (Runnable) player::closeInventory, 1);

        player.sendMessage(Messages.WRITE_FACTION_NAME_CHAT);
    }

    private static void continueCreation(String string, Player player) {
        string = string.replaceAll(" ", "_");

        if (factionals.getFactionByName(string) == null) {
            new Faction(string, factionals.getUserFromPlayer(player));

            player.sendMessage(Messages.SUCCESS);
        } else {
            player.sendMessage(Messages.FACTION_NAME_TAKEN.replaceAll("%1", string));
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
}
