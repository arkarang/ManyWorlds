package com.minepalm.manyworlds.api.bukkit;

public interface PreparedWorld {

    WorldInfo getWorldInfo();

    byte[] getWorldBytes();

    WorldMetadata getMetadata();

    void setWorldInfo(WorldInfo info);

    void setMetadata(WorldMetadata metadata);
}
