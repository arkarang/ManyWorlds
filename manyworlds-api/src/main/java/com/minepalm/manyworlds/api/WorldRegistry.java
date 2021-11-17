package com.minepalm.manyworlds.api;

import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import com.minepalm.manyworlds.api.bukkit.WorldDatabase;
import com.minepalm.manyworlds.api.bukkit.WorldEntity;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.WorldInform;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public interface WorldRegistry {

    @Nullable
    ManyWorld getWorld(WorldCategory category, String worldName);

    @Nullable
    ManyWorld getWorld(String worldName);

    ManyWorld getWorld(WorldInform inform);

    ManyWorld register(BukkitView view, WorldEntity entity);

    CompletableFuture<Boolean> isExist(WorldInform inform);

    CompletableFuture<Boolean> unregister(WorldInform inform);

    WorldDatabase getWorldDatabase(WorldCategory category);

    boolean registerDatabase(WorldDatabase database);
}
