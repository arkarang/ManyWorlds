package com.minepalm.manyworlds.bukkit.swm;

import com.grinderwolf.swm.nms.CraftSlimeWorld;
import com.minepalm.manyworlds.api.WorldProperties;
import com.minepalm.manyworlds.api.bukkit.WorldEntity;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.bukkit.strategies.WorldBuffer;
import lombok.Getter;

public class SWMWorldEntity extends CraftSlimeWorld implements WorldEntity {

    @Getter
    WorldInform worldInform;

    @Getter
    WorldProperties worldProperties;

    public SWMWorldEntity(SWMLoaderAdapter adapter, WorldInform info, WorldBuffer buffer){
        super(adapter,
                info.getName(),
                buffer.getChunks(),
                buffer.getExtraData(),
                buffer.getWorldMaps(),
                buffer.getWorldVersion(),
                buffer.getPropertyMap(), false, false);
        this.worldInform = info;
    }

}
