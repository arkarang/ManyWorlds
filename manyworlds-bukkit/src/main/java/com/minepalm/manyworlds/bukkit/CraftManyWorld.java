package com.minepalm.manyworlds.bukkit;

import com.flowpowered.nbt.CompoundTag;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeChunk;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.nms.CraftSlimeWorld;
import com.minepalm.manyworlds.api.bukkit.PreparedWorld;

import java.util.List;
import java.util.Map;

public class CraftManyWorld extends CraftSlimeWorld {

    public CraftManyWorld(SlimeLoader loader, String name, Map<Long, SlimeChunk> chunks, CompoundTag extraData, List<CompoundTag> worldMaps, byte version, SlimePropertyMap propertyMap, boolean readOnly, boolean locked) {
        super(loader, name, chunks, extraData, worldMaps, version, propertyMap, readOnly, locked);
    }

    public CraftManyWorld(PreparedWorld world){

    }
}
