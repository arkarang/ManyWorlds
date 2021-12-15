package com.minepalm.manyworlds.api;

import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import com.minepalm.manyworlds.api.bukkit.WorldEntity;
import com.minepalm.manyworlds.api.bukkit.WorldFactory;
import com.minepalm.manyworlds.api.entity.PreparedWorld;

import java.util.concurrent.CompletableFuture;

public interface WorldLoadService {

    CompletableFuture<WorldEntity> loadWorld(PreparedWorld world);

    CompletableFuture<PreparedWorld> unload(WorldEntity entity);

    WorldFactory getWorldFactory(WorldCategory type);

    boolean registerWorldFactory(WorldCategory type, WorldFactory loader);
}
