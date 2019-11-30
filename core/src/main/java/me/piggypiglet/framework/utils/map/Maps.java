package me.piggypiglet.framework.utils.map;

import javax.annotation.Nonnull;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Maps {
    private Maps() {
        throw new RuntimeException("This class cannot be instantiated.");
    }

    public static <K, V, R> Builder<K, V, R> builder(Map<K, V> implementation, @Nonnull R returnInstance, @Nonnull Consumer<Map<K, V>> build) {
        return new Builder<>(implementation, returnInstance, build);
    }

    public static <K, V> Builder<K, V, Map<K, V>> of() {
        return of(new HashMap<>());
    }

    public static <K, V> Builder<K, V, Map<K, V>> of(Map<K, V> implementation) {
        return new Builder<>(implementation);
    }

    public static final class Builder<K, V, R> {
        private final Map<K, V> map;
        private final R returnInstance;
        private final Consumer<Map<K, V>> build;

        private Builder(Map<K, V> implementation) {
            this(implementation, null, null);
        }

        private Builder(Map<K, V> implementation, R returnInstance, Consumer<Map<K, V>> build) {
            map = implementation;
            this.returnInstance = returnInstance;
            this.build = build;
        }

        public ValueBuilder key(K key) {
            return new ValueBuilder(key);
        }

        public final class ValueBuilder {
            private final K key;

            private ValueBuilder(K key) {
                this.key = key;
            }

            public Builder<K, V, R> value(V value) {
                map.put(key, value);
                return Builder.this;
            }
        }

        @SuppressWarnings("unchecked")
        public R build() {
            if (returnInstance != null && build != null) {
                build.accept(map);
                return returnInstance;
            }

            return (R) map;
        }
    }

    @SuppressWarnings("unchecked")
    public static Stream<Map.Entry<String, Object>> flatten(Map.Entry<String, Object> entry) {
        if (entry.getValue() instanceof Map) {
            return ((Map<String, Object>) entry.getValue()).entrySet().stream()
                    .map(e -> new AbstractMap.SimpleEntry<>(entry.getKey() + "." + e.getKey(), e.getValue()))
                    .flatMap(Maps::flatten);
        }

        return Stream.of(entry);
    }

    public static <T> T recursiveGet(Map<String, T> map, String path) {
        T object = map.getOrDefault(path, null);

        if (path.contains(".") && !path.startsWith(".") && !path.endsWith(".")) {
            String[] areas = path.split("\\.");
            object = map.getOrDefault(areas[0], null);

            if (areas.length >= 2 && object != null) {
                object = getBuriedObject(map, areas);
            }
        }

        return object;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getBuriedObject(Map<String, T> map, String[] keys) {
        int i = 1;
        Map<String, T> endObject = (Map<String, T>) map.get(keys[0]);

        while (instanceOfMap(map, endObject.get(keys[i]))) {
            endObject = (Map<String, T>) endObject.get(keys[i++]);
        }

        return endObject.get(keys[i]);
    }

    private static <T> boolean instanceOfMap(Map<String, T> map, Object obj) {
        if (obj instanceof Map) {
            final Set<?> keys = ((Map<?, ?>) obj).keySet();
            return keys.size() >= 1 && keys.toArray()[0] instanceof String;
        }

        return false;
    }
}