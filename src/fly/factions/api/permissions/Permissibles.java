package fly.factions.api.permissions;

import fly.factions.api.model.Permissible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Permissibles {
    private static Map<String, List<Permissible>> MAP = new HashMap<>();

    public static void add(String a, Permissible b) {
        MAP.putIfAbsent(a.toLowerCase(), new ArrayList<>());

        MAP.get(a.toLowerCase()).add(b);
    }

    public static void remove(Permissible a) {
        for(String s : MAP.keySet()) {
            MAP.get(s).remove(a);
        }
    }

    public static List<Permissible> get(String a) {
        return new ArrayList<>(MAP.getOrDefault(a.toLowerCase(), new ArrayList<>()));
    }
}
