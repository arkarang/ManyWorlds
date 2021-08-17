package com.minepalm.manyworlds.api;

import com.minepalm.manyworlds.api.bukkit.WorldInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

public interface GlobalDatabase {

    BungeeView getProxy();

    ServerView getCurrentServer();

    Future<ServerView> getServer(String name);

    Future<BukkitView> getBukkitServer(String name);

    Future<List<BukkitView>> getServers();

    Future<List<String>> getLoadedWorlds(String serverName);

    void register();

    void register(String name);

    void unregister();

    void unregister(String name);

    Future<Void> registerWorld(BukkitView snapshot, WorldInfo info);

    Future<Void> unregisterWorld(String fullName);

    Future<Void> unregisterWorld(String serverName, String sampleName, UUID uuid);

    Future<Void> resetWorlds(ServerView view);

    Future<Boolean> isWorldLoaded(WorldInfo info);

    Future<Boolean> isWorldLoaded(String fullName);

    Future<Optional<BukkitView>> getLoadedServer(WorldInfo info);

    Future<Boolean> isWorldLoaded(String serverName, String sampleName, UUID uuid);
}
