package com.minepalm.manyworlds.api.bukkit;

import com.grinderwolf.swm.api.world.SlimeWorld;

public interface ManyWorld extends SlimeWorld {

    WorldMetadata getMetaData();

    void setMetaData(WorldMetadata data);
}
