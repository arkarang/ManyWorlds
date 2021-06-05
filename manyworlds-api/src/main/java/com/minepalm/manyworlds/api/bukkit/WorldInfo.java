package com.minepalm.manyworlds.api.bukkit;

public interface WorldInfo {

    String getWorldName();

    WorldType getWorldType();

    WorldInfo clone();

}
