package com.minepalm.manyworlds.bukkit;

import com.grinderwolf.swm.nms.CraftSlimeWorld;
import com.minepalm.manyworlds.api.bukkit.ManyWorld;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldLoader;
import com.minepalm.manyworlds.api.bukkit.WorldMetadata;
import com.minepalm.manyworlds.bukkit.strategies.WorldBuffer;
import lombok.Getter;
import lombok.Setter;

public class CraftManyWorld extends CraftSlimeWorld implements ManyWorld {

    @Getter
    @Setter
    WorldMetadata metadata;

    @Getter
    WorldInfo worldInfo;

    CraftManyWorld(WorldInfo info, WorldMetadata metadata, WorldBuffer buffer) {
        this(ManyWorlds.getWorldLoader(info.getWorldType()), info, metadata, buffer);
    }

    CraftManyWorld(WorldLoader loader, WorldInfo info, WorldMetadata metadata, WorldBuffer buffer){
        super(loader,
                info.getWorldName(),
                buffer.getChunks(),
                buffer.getExtraData(),
                buffer.getWorldMaps(),
                buffer.getWorldVersion(),
                buffer.getPropertyMap(), false, false);
        this.worldInfo = info;
        this.metadata = metadata;
    }

}
