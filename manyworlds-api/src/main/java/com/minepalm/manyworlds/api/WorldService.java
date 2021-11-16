package com.minepalm.manyworlds.api;


import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.entity.WorldInform;

import java.util.concurrent.CompletableFuture;


public interface WorldService {

    WorldLoadService getLoadService();

    WorldEntityStorage getWorldEntityStorage();

    CompletableFuture<ManyWorld> createNewWorld(WorldInform info);

    CompletableFuture<ManyWorld> loadWorld(WorldInform info);

    CompletableFuture<Boolean> save(WorldInform info);

    CompletableFuture<Boolean> unload(WorldInform info);

}
