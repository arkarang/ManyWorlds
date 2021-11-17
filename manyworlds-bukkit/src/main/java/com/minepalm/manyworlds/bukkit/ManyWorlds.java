package com.minepalm.manyworlds.bukkit;

import com.minepalm.arkarangutils.bukkit.BukkitExecutor;
import com.minepalm.manyworlds.api.*;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.PreparedWorld;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.core.WorldTokens;
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
    final Controller controller;
    final WorldRegistry registry;
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
        this.controller = controller;
        this.registry = registry;
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
        return registry.getWorld(inform);
    }

    @Override
    public ManyWorld get(WorldCategory category, String worldName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<ManyWorld> createNewWorld(WorldInform origin, WorldInform info) {
        CompletableFuture<PreparedWorld> preparedWorldFuture = registry.getWorldDatabase(origin.getWorldCategory()).prepareWorld(origin.getName(), info);
        return preparedWorldFuture.thenCompose(preparedWorld->{
            if(preparedWorld != null)
                return loadService.loadWorld(preparedWorld).thenApply(worldEntity-> registry.register(this, worldEntity));
            else
                return CompletableFuture.completedFuture(null);
        });
    }

    @Override
    public CompletableFuture<ManyWorld> loadWorld(WorldInform info) {
        CompletableFuture<PreparedWorld> preparedWorldFuture = registry.getWorldDatabase(info.getWorldCategory()).prepareWorld(info);
        return preparedWorldFuture.thenCompose(preparedWorld->{
            if(preparedWorld != null)
                return loadService.loadWorld(preparedWorld).thenApply(worldEntity-> registry.register(this, worldEntity));
            else
                return CompletableFuture.completedFuture(null);
        });
    }

    @Override
    public CompletableFuture<Boolean> save(WorldInform info) {
        WorldEntity entity = worldEntityStorage.getLoadedWorld(info);
        return loadService.unload(entity)
                .thenCompose(preparedWorld -> executor.async(()-> registry.getWorldDatabase(info.getWorldCategory()).saveWorld(preparedWorld)))
                .thenApply(Objects::nonNull);
    }

    @Override
    public CompletableFuture<Boolean> unload(WorldInform info) {
        return executor.async(()->{
            World world = Bukkit.getWorld(info.getName());
            if(world != null) {
                Bukkit.unloadWorld(world, true);
                registry.unregister(info);
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
