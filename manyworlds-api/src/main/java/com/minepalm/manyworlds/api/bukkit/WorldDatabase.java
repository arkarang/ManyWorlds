package com.minepalm.manyworlds.api.bukkit;

import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.entity.PreparedWorld;

import java.util.concurrent.CompletableFuture;

public interface WorldDatabase {

    WorldCategory getCategory();

    CompletableFuture<PreparedWorld> prepareWorld(WorldInform info);

    CompletableFuture<PreparedWorld> prepareWorld(String get, WorldInform override);

    CompletableFuture<Void> saveWorld(PreparedWorld world);

    CompletableFuture<Boolean> exists(WorldInform inform);

    CompletableFuture<Void> deleteWorld(WorldInform info);

    CompletableFuture<Void> deleteWorld(String fullName);


}
