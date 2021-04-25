package fly.factions.impl.registries;

import fly.factions.api.registries.Registry;

import java.util.*;

public class RegistryImpl<V, K> implements Registry<V, K> {
    private Map<K, V> map = new HashMap<>();

    private Class<V> clazz;

    public RegistryImpl(Class<V> clazz) {
        this.clazz = clazz;
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public void set(K key, V value) {
        if(value != null) {
            map.put(key, value);
        } else {
            V v = map.remove(key);
        }
    }

    @Override
    public Collection<V> list() {
        return new ArrayList<>(map.values());
    }

    @Override
    public Map<K, V> map() {
        return new HashMap<>(map);
    }
}
