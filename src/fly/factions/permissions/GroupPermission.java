package fly.factions.permissions;

public enum GroupPermission {
    MEMBER_INVITE("citizenship-grant", "member-add"),
    MEMBER_KICK("citizenship-revoke", "member-add"),

    RANK_EDIT("rank-edit", "rank-edit"),

    LAND_CLAIM("land-claim", null),
    LAND_UNCLAIM("land-unclaim", null),
    VILLAGES("village", null),

    WITHDRAW("money-withdraw", "money-withdraw"),
    DEPOSIT("money-deposit", "money-deposit"),
    SET_TAXES("set-taxes", null),
    SET_WAGES("set-wages", "set-wages");

    public final String faction;
    public final String normal;

    GroupPermission(String faction, String normal) {
        this.faction = faction;
        this.normal = normal;
    }
}
