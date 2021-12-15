package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.entity.WorldInform;
import lombok.Getter;

@Getter
public class WorldCreatePacket extends BasicPacket {

    WorldInform origin;

    public WorldCreatePacket(WorldInform origin, WorldInform inform) {
        super(inform);
        this.origin = origin;
    }

}
