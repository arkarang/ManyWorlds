package com.minepalm.manyworlds.api.bukkit;

import java.util.UUID;

public interface WorldDatabase {

    PreparedWorld prepareWorld(WorldInfo info);

    void saveWorld(PreparedWorld world);

    WorldInfo getWorldInfo(String fullName);

    void deleteWorld(WorldInfo info);

    void deleteWorld(String fullName);


}
