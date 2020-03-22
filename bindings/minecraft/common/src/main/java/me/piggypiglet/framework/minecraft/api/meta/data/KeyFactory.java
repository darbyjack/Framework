package me.piggypiglet.framework.minecraft.api.meta.data;

import me.piggypiglet.framework.minecraft.api.meta.framework.Key;
import me.piggypiglet.framework.minecraft.api.meta.framework.KeyImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

public final class KeyFactory<H> {

    private KeyFactory() {}

    @NotNull
    public static <H> KeyFactory<H> initFactoryFromHandle() {
        return new KeyFactory<>();
    }

    @NotNull
    public <V> KeyImpl<V, H> ofNullable(@NotNull final Function<H, V> getter, @NotNull final KeyNames name) {
        return from(handle -> Optional.ofNullable(getter.apply(handle)), name);
    }

    @NotNull
    public <V> KeyImpl<V, H> of(@NotNull final Function<H, Optional<V>> getter, @NotNull final KeyNames name) {
        return from(handle -> {
            final Optional<V> result = getter.apply(handle);

            //noinspection OptionalAssignedToNull
            if (result == null) {
                return Optional.empty();
            }

            return result;
        }, name);
    }

    private static <V, H> KeyImpl<V, H> from(@NotNull final Function<H, Optional<V>> getter, @NotNull final KeyNames name) {
        return new KeyImpl<V, H>() {
            @NotNull
            @Override
            public Key<V, H> getKey() {
                return () -> name;
            }

            @NotNull
            @Override
            public Optional<V> get(@NotNull final H handle) {
                return getter.apply(handle);
            }
        };
    }
}
