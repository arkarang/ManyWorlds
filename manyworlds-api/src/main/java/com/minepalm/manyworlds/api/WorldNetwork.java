package com.minepalm.manyworlds.api;

import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface WorldNetwork {

    ServerView getCurrentServer();

    CompletableFuture<ServerView> getServer(String name);

    CompletableFuture<BukkitView> getBukkitServer(String name);

    CompletableFuture<List<BukkitView>> getServers();

    CompletableFuture<List<String>> getLoadedWorlds(String serverName);

    void registerServer();

    CompletableFuture<Void> registerServer(String name);

    void unregister();

    CompletableFuture<Void> unregisterServer(String name);

    CompletableFuture<Void> registerWorld(BukkitView snapshot, WorldInform info);

    CompletableFuture<Void> unregisterWorld(String fullName);

    CompletableFuture<Void> unregisterWorld(String serverName, String sampleName, UUID uuid);

    CompletableFuture<Void> resetWorlds(ServerView view);

    CompletableFuture<Boolean> isWorldLoaded(WorldInform info);

    CompletableFuture<Boolean> isWorldLoaded(String fullName);

    CompletableFuture<Optional<BukkitView>> getLoadedServer(WorldInform info);

    CompletableFuture<Boolean> isWorldLoaded(String serverName, String sampleName, UUID uuid);
}
