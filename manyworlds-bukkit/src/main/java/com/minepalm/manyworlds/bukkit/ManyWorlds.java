package com.minepalm.manyworlds.bukkit;

import com.minepalm.arkarangutils.bukkit.BukkitExecutor;
import com.minepalm.manyworlds.api.*;
import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import com.minepalm.manyworlds.api.bukkit.WorldEntity;
import com.minepalm.manyworlds.api.bukkit.WorldEntityStorage;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.PreparedWorld;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.netty.Controller;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Getter
public class ManyWorlds extends BukkitView implements WorldService {

    @Getter
    static ManyWorlds inst;

    final WorldLoadService loadService;
    final WorldEntityStorage worldEntityStorage;
    final WorldNetwork worldNetwork;
    final Controller worldController;
    final WorldRegistry worldRegistry;
    final BukkitExecutor executor;

    @Getter
    final String serverName;
    final Logger logger;

    ManyWorlds(String name,
               WorldNetwork worldNetwork,
               WorldController controller,
               WorldRegistry registry,
               WorldEntityStorage storage,
               WorldLoadService service,
               BukkitExecutor executor,
               Logger logger) {
        super(name, 0);
        if (inst == null)
            inst = this;
        else
            throw new IllegalStateException("ManyWorlds already exists");

        this.serverName = name;
        this.logger = logger;
        this.worldNetwork = worldNetwork;
        this.worldController = controller;
        this.worldRegistry = registry;
        this.executor = executor;
        this.loadService = service;
        this.worldEntityStorage = storage;

        worldNetwork.registerServer();
        worldNetwork.resetWorlds(this);

    }

    @Override
    public int getWorldCounts() {
        return worldEntityStorage.getCounts();
    }

    public void shutdown(){
        this.getWorldNetwork().unregister();
        //registry.shutdown();
        //worldEntityStorage.shutdown(loadService);
    }
    
    public BukkitView asView(){
        return this;
    }

    @Override
    public ManyWorld get(WorldInform inform) {
        return worldRegistry.getWorld(inform);
    }

    @Override
    public ManyWorld get(WorldCategory category, String worldName) {
        return get(new WorldInform(category, worldName));
    }

    @Override
    public CompletableFuture<ManyWorld> createNewWorld(WorldInform origin, WorldInform info) {
        CompletableFuture<PreparedWorld> preparedWorldFuture = worldRegistry.getWorldDatabase(origin.getWorldCategory()).prepareWorld(origin.getName(), info);
        return preparedWorldFuture.thenCompose(preparedWorld->{
            if(preparedWorld != null) {
                return loadService.loadWorld(preparedWorld).thenApply(worldEntity -> {
                    return worldRegistry.register(this, worldEntity);
                });
            }else {
                return CompletableFuture.completedFuture(null);
            }
        }).handle((mw, ex)->{
            if(ex != null){
                executor.sync((Runnable) ex::printStackTrace);
            }
            return mw;
        });
    }

    @Override
    public CompletableFuture<ManyWorld> loadWorld(WorldInform info) {
        CompletableFuture<PreparedWorld> preparedWorldFuture = worldRegistry.getWorldDatabase(info.getWorldCategory()).prepareWorld(info);
        return preparedWorldFuture.thenCompose(preparedWorld->{
            if(preparedWorld != null)
                return loadService.loadWorld(preparedWorld).thenApply(worldEntity-> worldRegistry.register(this, worldEntity));
            else
                return CompletableFuture.completedFuture(null);
        });
    }

    @Override
    public CompletableFuture<Boolean> save(WorldInform info) {
        WorldEntity entity = worldEntityStorage.getLoadedWorld(info);
        return loadService.unload(entity)
                .thenCompose(preparedWorld -> executor.async(()-> worldRegistry.getWorldDatabase(info.getWorldCategory()).saveWorld(preparedWorld)))
                .thenApply(Objects::nonNull);
    }

    @Override
    public CompletableFuture<Boolean> unload(WorldInform info) {
        return executor.sync(()->{
            World world = Bukkit.getWorld(info.getName());
            if(world != null) {
                Bukkit.unloadWorld(world, true);
                worldRegistry.unregister(info);
                return true;
            }else
                return false;
        });
    }

    @Override
    public CompletableFuture<ManyWorld> move(WorldInform inform, ServerView to) {
        //todo: implements this
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CompletableFuture<ManyWorld> copy(WorldInform origin, WorldInform to) {
        //todo: implements this
        throw new UnsupportedOperationException("not implemented");
    }


}
