package com.minepalm.manyworlds.api.bukkit;

public interface WorldInfo {

    String getSampleName();

    String getWorldName();

    String getGenerator();

    WorldType getWorldType();

    WorldInfo clone();

    long getLastUpdated();

    boolean isLocked();

}
