package fly.factions.inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Consumer;

public class CustomMenu {
    private Map<Integer, Button> buttons;

    public interface Button {
        void onLeftClick();

        void onRightClick();
    }
}
