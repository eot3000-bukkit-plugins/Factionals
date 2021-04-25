package fly.factions.api.permissions;

import fly.factions.api.model.Permissible;
import fly.factions.api.model.User;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum UserPermissible implements Permissible {
    EVERYONE {
        @Override
        public String getId() {
            return "public-everyone";
        }

        @Override
        public String getName() {
            return "Everyone";
        }

        @Override
        public boolean userHasPlotPermissions(User user, boolean owner, boolean pub) {
            return true;
        }

        @Override
        public ItemStack getItem() {
            return new ItemStack(Material.AIR);
        }
    },

    ALLY {
        @Override
        public String getId() {
            return "public-ally";
        }

        @Override
        public String getName() {
            return "Ally";
        }

        @Override
        public boolean userHasPlotPermissions(User user, boolean owner, boolean pub) {
            return true;
        }

        @Override
        public ItemStack getItem() {
            return new ItemStack(Material.AIR);
        }
    };

    UserPermissible() {
        Permissibles.add(getId(), this);
        Permissibles.add(getName(), this);
    }
}
