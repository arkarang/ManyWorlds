package com.minepalm.manyworlds.api.util;

import com.flowpowered.nbt.CompoundTag;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeChunk;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import lombok.Getter;
import lombok.Setter;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class WorldBuffer {

    byte version = -1;

    byte worldVersion = -1;

    String name;

    SlimeLoader loader;

    Map<Long, SlimeChunk> chunks = null;

    List<SlimeChunk> sortedChunks = null;

    CompoundTag extraData = null;

    BitSet chunkBitSet = null;

    List<CompoundTag> worldMaps = null;

    SlimePropertyMap propertyMap;

    public boolean isComplete() {
        return this.getName() != null
                && this.loader != null
                && this.chunks != null
                && this.extraData != null
                && this.version != -1
                && this.worldMaps != null
                && this.worldVersion != -1;
    }

    public void release(){
        name = null;
        version = 0;
        worldVersion = 0;
        loader = null;
        chunks = null;
        sortedChunks = null;
        extraData = null;
        chunkBitSet = null;
        worldMaps = null;
        propertyMap = null;
    }
}
