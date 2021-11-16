package com.minepalm.manyworlds.bukkit;

import com.minepalm.arkarangutils.bukkit.BukkitExecutor;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.WorldLoadService;
import com.minepalm.manyworlds.api.WorldRegistry;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.entity.PreparedWorld;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

@RequiredArgsConstructor
public abstract class AbstractWorldLoadService implements WorldLoadService {

    private final ExecutorService service;
    private final Logger logger;

    private final ConcurrentHashMap<WorldCategory, WorldFactory> factories = new ConcurrentHashMap<>();

    protected abstract WorldEntity finalizeWorldLoad(WorldFactory factory, WorldEntity entity);

    protected abstract PreparedWorld finalizeWorldUnload(WorldFactory factory, PreparedWorld preparedWorld);

    @Override
    public CompletableFuture<WorldEntity> loadWorld(PreparedWorld world) {
        return CompletableFuture.supplyAsync(()->{
            try {
                WorldInform inform = world.getWorldInform();
                WorldFactory factory =  getWorldFactory(inform.getWorldCategory());
                return finalizeWorldLoad(factory, factory.deserialize(world));
            }catch (IOException e){
                logger.severe("World "+world.getWorldInform().getName()+" could not be created by: "+ e.getMessage());
            }
            return null;
        }, service);
    }

    @Override
    public CompletableFuture<PreparedWorld> unload(WorldEntity entity) {
        return CompletableFuture.supplyAsync(()->{
            try {
                WorldInform inform = entity.getWorldInform();
                WorldFactory factory =  getWorldFactory(inform.getWorldCategory());
                return finalizeWorldUnload(factory, factory.serialize(entity));
            }catch (IOException e){
                logger.severe("World "+entity.getWorldInform().getName()+" could not be created by: "+ e.getMessage());
            }
            return null;
        }, service);
    }

    @Override
    public WorldFactory getWorldFactory(WorldCategory type){
        if(factories.containsKey(type))
            return factories.get(type);
        else
            throw new IllegalArgumentException("loader (WorldCategory: "+type.getName()+") does not exists");
    }

    @Override
    public boolean registerWorldFactory(WorldCategory type, WorldFactory loader) {
        boolean exists = factories.containsKey(type);
        if(!exists)
            factories.put(type, loader);
        return !exists;
    }
}
