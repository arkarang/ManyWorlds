package com.minepalm.manyworlds.bukkit;

import com.minepalm.manyworlds.api.ManyWorld;
import com.minepalm.manyworlds.api.WorldController;
import com.minepalm.manyworlds.api.WorldNetwork;
import com.minepalm.manyworlds.api.WorldRegistry;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractManyWorld implements ManyWorld{

    WorldRegistry registry;
    WorldNetwork worldNetwork;
    WorldController controller;
    WorldInform info;

    AbstractManyWorld(@Nonnull WorldInform info, WorldRegistry registry, WorldNetwork worldNetwork, WorldController controller){
        this.info = info;
        this.registry = registry;
        this.worldNetwork = worldNetwork;
        this.controller = controller;
    }

    @Override
    public String getName() {
        return info.getName();
    }
    @Override
    public WorldInform getWorldInfo() {
        return info;
    }

    @Override
    public CompletableFuture<ManyWorld> create(@Nonnull BukkitView view, WorldInform inform) {
        return isExists().thenCompose(exists->{
            if(!exists){
                return controller.createSpecific(view, inform, info);
            }else {
                return CompletableFuture.completedFuture(null);
            }
        });
    }

    @Override
    public CompletableFuture<ServerView> create(WorldInform inform) {
        return isExists().thenCompose(exists->{
            if(!exists){
                return controller.createAtLeast(inform, info);
            }else {
                return CompletableFuture.completedFuture(null);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isExists() {
        return this.registry.isExist(info);
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
            if(!isLoaded){
                return controller.loadAtLeast(this.info);
            }else{
                return CompletableFuture.completedFuture(ManyWorlds.getInst());
            }
        });
    }

    @Override
    public CompletableFuture<ServerView> load(BukkitView server) {
        return isLoaded().thenCompose(isLoaded -> {
            if(!isLoaded){
                return controller.updateSpecific(server, this.info, true).thenCompose(ManyWorld::getLocated).thenApply(Optional::get);
            }else{
                return CompletableFuture.completedFuture(ManyWorlds.getInst());
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> unload() {
        return isLoaded().thenCompose(isLoaded -> {
            if (isLoaded) {
                return this.getLocated().thenApply(located->{
                    if(located.isPresent()){
                        controller.updateSpecific(located.get(), this.info, false);
                        return true;
                    }
                    return false;
                });
            } else {
                return CompletableFuture.completedFuture(true);
            }
        });
    }

}
