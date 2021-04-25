package fly.factions.api.model;

import fly.factions.api.permissions.FactionPermission;

/**
 * The {@code ExecutiveDivision} class represents faction departments
 */
public interface ExecutiveDivision extends FactionComponent {

    /**
     * Adds a permission to the division
     *
     * @see ExecutiveDivision#canDo(FactionPermission)
     *
     * @param permission the permission to add
     */

    void addPermission(FactionPermission permission);

    /**
     * Removes a permission from the division
     *
     * @see ExecutiveDivision#canDo(FactionPermission)
     *
     * @param permission the permission to remove
     */

    void removePermission(FactionPermission permission);

    /**
     * Checks if the division has a specific permission
     *
     * @param permission the permission to checl
     * @return {@code true} if the division has the permission, {@code false} if not
     */

    boolean canDo(FactionPermission permission);
}
