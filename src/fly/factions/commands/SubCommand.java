package fly.factions.commands;

import fly.factions.permissions.GroupPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class SubCommand {
    Predicate<CommandInfo> predicate;
    Function<CommandInfo, Boolean> function;
    List<SecondaryPredicate> also = new ArrayList<>();
    List<PermissionPredicate> permissions = new ArrayList<>();

    public SubCommand also(int arg, String message, Predicate<String> check) {
        SecondaryPredicate predicate = new SecondaryPredicate();

        predicate.arg = arg-1;
        predicate.message = message;
        predicate.check = check;

        also.add(predicate);

        return this;
    }

    public SubCommand groupPermission(int arg, GroupPermission permission) {
        PermissionPredicate predicate = new PermissionPredicate();

        predicate.arg = arg-1;
        predicate.permission = permission;

        return this;
    }

    static class SecondaryPredicate {
        int arg;
        boolean player;
        String message;
        Predicate<?> check;
    }

    static class PermissionPredicate {
        int arg;
        GroupPermission permission;
    }
}
