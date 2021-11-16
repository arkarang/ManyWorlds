package com.minepalm.manyworlds.bukkit;

import com.minepalm.manyworlds.api.ManyWorld;
import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class LocalManyWorld implements ManyWorld {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Optional<String> getType() {
        return Optional.empty();
    }

    @Override
    public WorldInform getWorldInfo() {
        return null;
    }

    @Override
    public CompletableFuture<Optional<BukkitView>> getLocated() {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> isLoaded() {
        return null;
    }

    @Override
    public CompletableFuture<ServerView> load() {
        return null;
    }

    @Override
    public CompletableFuture<ServerView> load(BukkitView server) {
        return null;
    }

    @Override
    public CompletableFuture<ServerView> move(BukkitView view) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> unload() {
        return null;
    }

    @Override
    public CompletableFuture<ManyWorld> copy(WorldCategory type, String worldName) {
        return null;
    }
}
