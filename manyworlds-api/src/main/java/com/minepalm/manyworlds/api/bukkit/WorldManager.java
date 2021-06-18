package com.minepalm.manyworlds.api.bukkit;

public interface WorldManager {

    WorldDatabase getWorldDatabase(WorldType type);

    WorldStorage getWorldStorage();

    WorldLoader getWorldLoader(WorldType type);

    void createNewWorld(WorldInfo info);

    void loadWorld(WorldInfo info);

    void save(WorldInfo info);

    void save(String worldFullName);

}
