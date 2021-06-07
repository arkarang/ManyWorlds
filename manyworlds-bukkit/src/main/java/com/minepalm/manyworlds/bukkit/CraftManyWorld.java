package com.minepalm.manyworlds.bukkit;

import com.flowpowered.nbt.CompoundTag;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeChunk;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.nms.CraftSlimeWorld;
import com.minepalm.manyworlds.api.bukkit.ManyWorld;
import com.minepalm.manyworlds.api.bukkit.PreparedWorld;
import com.minepalm.manyworlds.api.bukkit.WorldFactory;
import com.minepalm.manyworlds.api.bukkit.WorldMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class CraftManyWorld extends CraftSlimeWorld implements ManyWorld {

    @Getter
    @Setter
    WorldMetadata metadata;

   CraftManyWorld(WorldFactory factory){
        super(factory.getSlimeLoader(), factory.getName(), factory.getChunks(), factory.getExtraData(), factory.getWorldMaps(), factory.getVersion(), factory.getPropertyMap(), factory.isReadOnly(), factory.isLocked());
    }

    @Override
    public Map<Long, SlimeChunk> getChunks() {
        return super.getChunks();
    }
}
