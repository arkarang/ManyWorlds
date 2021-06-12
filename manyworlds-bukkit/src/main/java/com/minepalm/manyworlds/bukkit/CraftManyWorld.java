package com.minepalm.manyworlds.bukkit;

import com.grinderwolf.swm.nms.CraftSlimeWorld;
import com.minepalm.manyworlds.api.bukkit.ManyWorld;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldMetadata;
import com.minepalm.manyworlds.api.util.WorldBuffer;
import lombok.Getter;
import lombok.Setter;

public class CraftManyWorld extends CraftSlimeWorld implements ManyWorld {

    @Getter
    @Setter
    WorldMetadata metadata;

    @Getter
    WorldInfo worldInfo;

   CraftManyWorld(WorldInfo info, WorldBuffer buffer){
        super(ManyWorldsBukkit.getInst().getWorldLoader(info.getWorldType()), info.getWorldName(), buffer.getChunks(), buffer.getExtraData(), buffer.getWorldMaps(), buffer.getVersion(), buffer.getPropertyMap(), false, false);
    }

}
