package com.minepalm.manyworlds.bukkit;

import com.minepalm.manyworlds.api.ManyWorld;
import com.minepalm.manyworlds.api.WorldController;
import com.minepalm.manyworlds.api.WorldNetwork;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractManyWorld implements ManyWorld{

    WorldNetwork worldNetwork;
    WorldController controller;
    WorldInform info;

    AbstractManyWorld(@Nonnull WorldInform info, WorldNetwork worldNetwork, WorldController controller){
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
        return isLoaded().thenCompose(isLoaded -> {
            if(isLoaded){
                return controller.loadAtLeast(this.info);
            }else{
                return CompletableFuture.completedFuture(ManyWorlds.getInst().asView());
            }
        });
    }

    @Override
    public CompletableFuture<ServerView> load(BukkitView server) {
        return isLoaded().thenCompose(isLoaded -> {
            if(isLoaded){
                return controller.updateSpecific(server, this.info, true).thenCompose(ManyWorld::getLocated).thenApply(Optional::get);
            }else{
                return CompletableFuture.completedFuture(ManyWorlds.getInst().asView());
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> unload() {
        return null;
    }

}
