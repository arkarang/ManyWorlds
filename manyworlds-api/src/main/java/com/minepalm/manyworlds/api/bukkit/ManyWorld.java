package com.minepalm.manyworlds.api.bukkit;

import com.flowpowered.nbt.CompoundTag;
import com.grinderwolf.swm.api.world.SlimeChunk;
import com.grinderwolf.swm.api.world.SlimeWorld;

import java.util.List;
import java.util.Map;

public interface ManyWorld extends SlimeWorld {

    WorldInfo getWorldInfo();

    WorldMetadata getMetadata();

    void setMetadata(WorldMetadata data);


}
