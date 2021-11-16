package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import lombok.Getter;

@Getter
public class WorldLoadPacket extends BasicPacket{

    private final boolean load;

    public WorldLoadPacket(ServerView from, ServerView to, WorldInform info, boolean load) {
        super(from, to, info);
        this.load = load;
    }

}
