package com.minepalm.manyworlds.bukkit;

import com.minepalm.manyworlds.api.*;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.ServerView;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ProxiedManyWorld implements ManyWorld {

    WorldNetwork worldNetwork;
    WorldController controller;
    WorldInform info;

    ProxiedManyWorld(@Nonnull WorldInform info, WorldNetwork worldNetwork, WorldController controller){
        this.info = info;
        this.worldNetwork = worldNetwork;
        this.controller = controller;
    }

    @Override
    public String getName() {
        return info.getName();
    }

    @Override
    public Optional<String> getType() {
        return Optional.of(info.getType());
    }

    @Override
    public WorldInform getWorldInfo() {
        return info;
    }

    @Override
    public CompletableFuture<Optional<BukkitView>> getLocated() {
        return worldNetwork.getLoadedServer(this.info);
    }

    @Override
    public CompletableFuture<Boolean> isLoaded() {
        return worldNetwork.getLoadedServer(this.info).thenApply(Optional::isPresent);
    }

    @Override
    public CompletableFuture<ServerView> load() {
        return controller.loadAtLeast(this.info);
    }

    @Override
    public CompletableFuture<ServerView> load(BukkitView server) {
        return controller.updateSpecific(server, this.info, true).thenCompose(ManyWorld::getLocated).thenApply(Optional::get);
    }

    @Override
    public CompletableFuture<ServerView> move(BukkitView view) {
        worldNetwork.isWorldLoaded(this.info).thenApply(isLoaded->{
            if(isLoaded){

            }
            return null;
        });
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
