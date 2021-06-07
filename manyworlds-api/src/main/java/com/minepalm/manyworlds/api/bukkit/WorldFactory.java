package com.minepalm.manyworlds.api.bukkit;

import com.flowpowered.nbt.CompoundTag;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeChunk;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;

import java.util.List;
import java.util.Map;

public interface WorldFactory {

    WorldLoader getWorldLoader();

    SlimeLoader getSlimeLoader();

    String getName();

    Map<Long, SlimeChunk> getChunks();

    CompoundTag getExtraData();

    List<CompoundTag> getWorldMaps();

    byte getVersion();

    SlimePropertyMap getPropertyMap();

    boolean isReadOnly();

    boolean isLocked();

    ManyWorld build();
}
