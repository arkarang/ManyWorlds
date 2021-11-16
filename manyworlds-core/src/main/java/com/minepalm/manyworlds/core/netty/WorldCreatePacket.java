package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import lombok.Getter;

@Getter
public class WorldCreatePacket extends BasicPacket {

    public WorldCreatePacket(ServerView from, ServerView to, WorldInform inform) {
        super(from, to, inform);
    }

}
