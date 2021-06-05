package com.minepalm.manyworlds.api.bukkit;

import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;

public interface PreparedWorld {

    WorldInfo getWorldInfo();

    byte[] getWorldBytes();

    SlimePropertyMap getProperties();

    WorldMetadata getMetadata();
}
