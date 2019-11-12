package me.piggypiglet.framework.minecraft.server;

import java.util.Set;
import java.util.UUID;

public interface Server<T> {
    String getIP();

    int getPort();

    Set<String> getIPBans();

    Set<UUID> getBannedPlayers();

    String getVersion();

    String getImplementationVersion();

    Set<UUID> getOperators();

    int getMaxPlayers();

    boolean isOnlineMode();

    Set<UUID> getPlayers();

    Set<UUID> getWorlds();

    T getHandle();
}
