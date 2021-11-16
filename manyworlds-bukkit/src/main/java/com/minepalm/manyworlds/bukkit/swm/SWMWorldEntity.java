package com.minepalm.manyworlds.bukkit.swm;

import com.grinderwolf.swm.nms.CraftSlimeWorld;
import com.minepalm.manyworlds.api.WorldProperties;
import com.minepalm.manyworlds.api.bukkit.WorldEntity;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.bukkit.WorldFactory;
import com.minepalm.manyworlds.bukkit.strategies.WorldBuffer;
import lombok.Getter;

public class SWMWorldEntity extends CraftSlimeWorld implements WorldEntity {

    @Getter
    WorldInform worldInform;

    @Getter
    WorldProperties properties;

    public SWMWorldEntity(WorldFactory loader, WorldInform info, WorldBuffer buffer){
        super((SWMWorldFactory)loader,
                info.getName(),
                buffer.getChunks(),
                buffer.getExtraData(),
                buffer.getWorldMaps(),
                buffer.getWorldVersion(),
                buffer.getPropertyMap(), false, false);
        this.worldInform = info;
    }

}
