package com.minepalm.manyworlds.bukkit;

import com.minepalm.manyworlds.api.ManyWorld;
import com.minepalm.manyworlds.api.WorldController;
import com.minepalm.manyworlds.api.WorldNetwork;
import com.minepalm.manyworlds.api.WorldRegistry;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.entity.WorldInform;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class ManyWorldRegistry implements WorldRegistry {

    final WorldNetwork worldNetwork;
    final WorldEntityStorage storage;
    final WorldController controller;
    final ConcurrentHashMap<WorldCategory, WorldDatabase> databases = new ConcurrentHashMap<>();

    @Override
    public ManyWorld getWorld(WorldInform inform) {
        if(storage.getLoadedWorld(inform) != null){
            return new LocalManyWorld();
        }else
            return new ProxiedManyWorld(inform, worldNetwork, controller);
    }

    @Override
    public ManyWorld register(WorldInform inform, WorldEntity entity) {
        storage.registerWorld(entity);
        return null;
    }

    @Override
    public void unregister(WorldInform inform) {

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
