package com.minepalm.manyworlds.api;

import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;

public interface WorldProperties{

    int getSpawnX();

    int getSpawnY();

    int getSpawnZ();

    boolean isAllowAnimals();

    boolean isAllowMonsters();

    String getEnvironment();

    String getWorldType();

    String getDifficulty();

    SlimePropertyMap asSlime();
}
