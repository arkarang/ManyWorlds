package com.minepalm.manyworlds.api.bukkit;

import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.WorldLoadService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WorldEntityStorage {

    CompletableFuture<Void> registerWorld(WorldEntity world);

    WorldEntity getLoadedWorld(String name);

    List<String> getLoadedWorldsAll();

    WorldEntity getLoadedWorld(WorldInform info);

    WorldEntity unregisterWorld(String name);


    int getCounts();

    int getMaximumCounts();

}
