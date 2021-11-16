package com.minepalm.manyworlds.api;

import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import com.minepalm.manyworlds.api.bukkit.WorldDatabase;
import com.minepalm.manyworlds.api.bukkit.WorldEntity;
import com.minepalm.manyworlds.api.entity.WorldInform;

public interface WorldRegistry {

    ManyWorld getWorld(WorldInform inform);

    ManyWorld register(WorldInform inform, WorldEntity entity);

    void unregister(WorldInform inform);

    WorldDatabase getWorldDatabase(WorldCategory category);

    boolean registerDatabase(WorldDatabase database);
}
