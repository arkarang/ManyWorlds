package com.minepalm.manyworlds.api.bukkit;

// name / byte[] / propertyMap / metadata / lastUsed
public interface WorldDatabase {

    PreparedWorld prepareWorld(WorldInfo info);

    void saveWorld(PreparedWorld world);
}
