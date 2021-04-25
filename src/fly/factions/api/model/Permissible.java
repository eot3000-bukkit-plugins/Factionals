package fly.factions.api.model;

public interface Permissible extends MenuListable, Savable {
    String getId();

    String getName();

    boolean userHasPlotPermissions(User user, boolean owner, boolean pub);
}
