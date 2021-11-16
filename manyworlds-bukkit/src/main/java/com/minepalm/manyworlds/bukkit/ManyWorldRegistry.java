package com.minepalm.manyworlds.bukkit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.minepalm.manyworlds.api.ManyWorld;
import com.minepalm.manyworlds.api.WorldController;
import com.minepalm.manyworlds.api.WorldNetwork;
import com.minepalm.manyworlds.api.WorldRegistry;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.entity.WorldInform;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class ManyWorldRegistry implements WorldRegistry {

    final WorldNetwork worldNetwork;
    final WorldEntityStorage storage;
    final WorldController controller;
    final ConcurrentHashMap<WorldCategory, WorldDatabase> databases = new ConcurrentHashMap<>();

    @Override
    public ManyWorld getWorld(String worldName) {
        WorldEntity entity = storage.getLoadedWorld(worldName);
        World world = Bukkit.getWorld(worldName);
        if(entity != null && world != null){
            return new LocalManyWorld(entity.getWorldInform(), worldNetwork, controller);
        }else
            return null;
    }

    @Override
    public ManyWorld getWorld(WorldInform inform) {
        WorldEntity entity = storage.getLoadedWorld(inform);
        World world = Bukkit.getWorld(inform.getName());
        if(entity != null && world != null){
            return new LocalManyWorld(entity.getWorldInform(), worldNetwork, controller);
        }else
            return new ProxiedManyWorld(inform, worldNetwork, controller);
    }

    @Override
    public ManyWorld register(WorldInform inform, WorldEntity entity) {
        storage.registerWorld(entity);
        return null;
    }

    @Override
    public CompletableFuture<Boolean> unregister(WorldInform inform) {
        WorldEntity entity = storage.unregisterWorld(inform.getName());
        if(entity != null)
            return worldNetwork.unregisterWorld(inform.getName()).thenApply(ignored->true);
        else
            return CompletableFuture.completedFuture(false);
    }

    @Override
    @Nullable
    public WorldDatabase getWorldDatabase(WorldCategory type) {
        return databases.get(type);
    }

    @Override
    public boolean registerDatabase(WorldDatabase database) {
        if(!databases.containsKey(database.getCategory())) {
            databases.put(database.getCategory(), database);
            return true;
        }else{
            return false;
        }
    }
}
