package fly.factions.serialization;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Serializer<T extends Savable> {
    private static Map<Class<? extends Savable>, Serializer<?>> SERIALIZERS = new HashMap<>();

    public Serializer(Class<T> clazz) {
        SERIALIZERS.put(clazz, this);
    }

    public static void saveAll(List<Savable> list) {
        for(Savable savable : list) {
            save0(savable);
        }
    }

    private static <X extends Savable>void save0(X savable) {
        Serializer<X> serializer = (Serializer<X>) SERIALIZERS.get(savable.getClass());

        serializer.save(savable);
    }

    public abstract File dir();

    public abstract void save(T t);

    public abstract T load(File file);
}
