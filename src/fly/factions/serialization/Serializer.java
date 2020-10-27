package fly.factions.serialization;

import fly.factions.Factionals;

import java.io.File;
import java.util.*;

public abstract class Serializer<T extends Savable> {
    private static Map<Class<? extends Savable>, Serializer<?>> SERIALIZERS = new HashMap<>();
    protected Factionals factionals;

    public Serializer(Class<T> clazz) {
        SERIALIZERS.put(clazz, this);
    }

    public static void saveAll(Collection<? extends Savable> list) {
        for(Savable savable : list) {
            save0(savable);
        }
    }

    public static <X extends Savable>Collection<X> loadAll(Class<X> clazz) {
        Serializer<X> serializer = (Serializer<X>) SERIALIZERS.get(clazz);
        List<X> list = new ArrayList<>();

        serializer.dir().mkdirs();

        serializer.factionals = Factionals.getFactionals();

        for(File file : serializer.dir().listFiles()) {
            list.add(serializer.load(file));
        }

        return list;
    }

    private static <X extends Savable>void save0(X savable) {
        Serializer<X> serializer = (Serializer<X>) SERIALIZERS.get(savable.getClass());

        serializer.save(savable);
    }

    public abstract File dir();

    public abstract void save(T t);

    public abstract T load(File file);
}
