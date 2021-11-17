package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.entity.WorldInform;
import lombok.Getter;

public class WorldCopyPacket extends BasicPacket{

    @Getter
    final WorldInform toCopy;

    public WorldCopyPacket(WorldInform inform, WorldInform toCopy) {
        super(inform);
        this.toCopy = toCopy;
    }
}
