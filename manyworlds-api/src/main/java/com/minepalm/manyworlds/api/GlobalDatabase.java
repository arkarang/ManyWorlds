package com.minepalm.manyworlds.api;

import com.minepalm.manyworlds.api.bukkit.WorldInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GlobalDatabase {

    BungeeView getProxy();

    ServerView getCurrentServer();

    ServerView getServer(String name);

    BukkitView getBukkitServer(String name);

    List<BukkitView> getServers();

    List<String> getLoadedWorlds(String serverName);

    void register();

    void unregister();

    void registerWorld(BukkitView snapshot, WorldInfo info);

    void unregisterWorld(String fullName);

    void unregisterWorld(String serverName, String sampleName, UUID uuid);

    void resetWorlds(ServerView view);

    boolean isWorldLoaded(WorldInfo info);

    boolean isWorldLoaded(String fullName);

    Optional<BukkitView> getLoadedServer(WorldInfo info);

    boolean isWorldLoaded(String serverName, String sampleName, UUID uuid);
}
