package fly.factions.api.model;

import java.util.Collection;

/**
 * The {@code Faction} class represents factions
 */
public interface Faction extends LandAdministrator {

    /**
     * @return {@code true} if the faction was deleted, or {@code false} if not
     */
    boolean isDeleted();

    /**
     * @return a collection of all the faction's regions
     */

    Collection<Region> getRegions();

    /**
     * Returns the faction's region with a specific name
     *
     * @param s the name of the region
     *
     * @return the region in the faction with the specified name, or {@code null} if no such region exists
     */

    Region getRegion(String s);

    /**
     * Adds a region to the faction. If the region's faction isn't the same one as this one, the method will ignore the method call
     *
     * @param region the region to be added
     */

    void addRegion(Region region);

    /**
     * Removes a region from the faction.
     *
     * @param region the region to remove
     */

    void removeRegion(Region region);

    /**
     * @return a collection of all the faction's executive divisions
     */

    Collection<ExecutiveDivision> getDepartments();

    /**
     * Returns the faction's executive division with a specific name
     *
     * @param s the name of the executive division
     *
     * @return the executive division in the faction with the specified name, or {@code null} if no such division exists
     */

    ExecutiveDivision getDepartment(String s);

    /**
     * Adds an executive division to the faction. If the divisions's faction isn't the same one as this one, the method will ignore the method call
     *
     * @param division the division to be added
     */

    void addDepartment(ExecutiveDivision division);

    /**
     * Removes an executive division from the faction.
     *
     * @param division the region to remove
     */

    void removeDepartment(ExecutiveDivision division);

    /**
     * Deletes the faction, and removes it from any registries
     */

    void delete();

    /**
     * Adds a user to the faction's member list.
     * This method should only be used by inheritors of {@code User}
     *
     * @see User#setFaction(Faction)
     *
     * @param user the user to add
     */

    @Deprecated
    void addMember(User user);

    /**
     * Removes a user from the faction's member list, and the member lists of the faction's components.
     * This method should only be used by inheritors of {@code User}
     *
     * @see User#setFaction(Faction)
     *
     * @param user the user to remove
     */

    @Deprecated
    void removeMember(User user);
}
