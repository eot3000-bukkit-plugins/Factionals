package fly.factions.api.permissions;


public enum FactionPermission {
    //Every permission, including faction disbanding
    OWNER,

    //Create ranks and regions. Assign perms to ranks, and land to regions
    INTERNAL_MANAGEMENT,

    //Claim/unclaim land
    TERRITORY,

    //Declare allies, enemies, etc
    RELATIONS,

    //Invite and kick players
    USERS,
}
