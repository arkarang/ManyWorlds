package com.minepalm.manyworlds.api;

public interface WorldProperties{

    int getSpawnX();

    int getSpawnY();

    int getSpawnZ();

    boolean isAllowAnimals();

    boolean isAllowMonsters();

    String getEnvironment();

    String getWorldType();

    String getDifficulty();

}
