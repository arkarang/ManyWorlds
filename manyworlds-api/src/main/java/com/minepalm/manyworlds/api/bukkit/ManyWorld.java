package com.minepalm.manyworlds.api.bukkit;

import com.grinderwolf.swm.api.world.SlimeWorld;

public interface ManyWorld extends SlimeWorld {

    WorldInfo getWorldInfo();

    WorldMetadata getMetadata();

    void setMetadata(WorldMetadata data);


}
