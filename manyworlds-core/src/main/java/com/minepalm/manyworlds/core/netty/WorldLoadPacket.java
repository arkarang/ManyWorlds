package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import lombok.Getter;

@Getter
public class WorldLoadPacket extends BasicPacket{

    private final boolean load;

    public WorldLoadPacket(WorldInform info, boolean load) {
        super(info);
        this.load = load;
    }

}
