package com.minepalm.manyworlds.api.bukkit;

import java.util.concurrent.Future;

public interface WorldManager {

    WorldDatabase getWorldDatabase(WorldType type);

    WorldStorage getWorldStorage();

    WorldLoader getWorldLoader(WorldType type);

    Future<Void> createNewWorld(WorldInfo info);

    Future<Void>  loadWorld(WorldInfo info);

    Future<Void>  save(WorldInfo info);

    Future<Void>  save(String worldFullName);

}
