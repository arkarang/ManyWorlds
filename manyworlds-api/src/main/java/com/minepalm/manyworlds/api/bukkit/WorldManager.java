package com.minepalm.manyworlds.api.bukkit;


import java.util.concurrent.Future;

public interface WorldManager {

    WorldDatabase getWorldDatabase(WorldType type);

    WorldStorage getWorldStorage();

    WorldLoader getWorldLoader(WorldType type);

    boolean registerWorldLoader(WorldType type, WorldLoader loader);

    Future<Void> createNewWorld(WorldInfo info);

    Future<Void> createNewWorld(WorldInfo info, Runnable run);

    Future<Void> loadWorld(WorldInfo info);

    Future<Void> loadWorld(WorldInfo info, Runnable run);

    Future<Void> save(WorldInfo info);

    Future<Void> save(String worldFullName);

    Future<Void> unload(WorldInfo info);

    Future<Void> unload(String worldFullName);

}
