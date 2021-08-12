package com.minepalm.manyworlds.api.bukkit;

import com.minepalm.manyworlds.api.errors.LoaderNotFoundException;

import java.util.concurrent.Future;

public interface WorldManager {

    WorldDatabase getWorldDatabase(WorldType type);

    WorldStorage getWorldStorage();

    WorldLoader getWorldLoader(WorldType type) throws LoaderNotFoundException;

    boolean registerWorldLoader(WorldType type, WorldLoader loader);

    Future<Void> createNewWorld(WorldInfo info);

    Future<Void> createNewWorld(WorldInfo info, Runnable run);

    Future<Void> loadWorld(WorldInfo info);

    Future<Void> loadWorld(WorldInfo info, Runnable run);

    Future<Void> save(WorldInfo info);

    Future<Void> save(String worldFullName);

}
