package com.minepalm.manyworlds.bukkit;

import com.minepalm.arkarangutils.bukkit.BukkitExecutor;
import com.minepalm.manyworlds.api.*;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.PreparedWorld;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import com.minepalm.manyworlds.bukkit.mysql.MySQLWorldTypeDatabase;
import com.minepalm.manyworlds.bukkit.swm.SWMWorldFactory;
import com.minepalm.manyworlds.core.WorldTokens;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Getter
public class ManyWorlds implements WorldService {

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
        worldNetwork.resetWorlds(this.asView());

    }

    public void shutdown(){
        this.getWorldNetwork().unregister();
        //registry.shutdown();
        //worldEntityStorage.shutdown(loadService);
    }
    
    public BukkitView asView(){
        return new BukkitView(serverName, getWorldEntityStorage().getCounts());
    }

    public void send(WorldPacket packet){
        this.getController().send(packet);
    }

    @Override
    public CompletableFuture<ManyWorld> createNewWorld(WorldInform info) {
        CompletableFuture<PreparedWorld> preparedWorldFuture = registry.getWorldDatabase(WorldTokens.TYPE).prepareWorld(info);
        CompletableFuture<WorldEntity> worldEntityFuture = preparedWorldFuture.thenCompose(loadService::loadWorld);
        return worldEntityFuture.thenApply(worldEntity-> registry.register(info, worldEntity));
    }

    @Override
    public CompletableFuture<ManyWorld> loadWorld(WorldInform info) {
        CompletableFuture<PreparedWorld> preparedWorldFuture = registry.getWorldDatabase(info.getWorldCategory()).prepareWorld(info);
        CompletableFuture<WorldEntity> worldEntityFuture = preparedWorldFuture.thenCompose(loadService::loadWorld);
        return worldEntityFuture.thenApply(worldEntity-> registry.register(info, worldEntity));
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


}
