package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import lombok.Getter;

public class WorldMovePacket extends BasicPacket{

    @Getter
    final ServerView toMove;

    public WorldMovePacket(WorldInform inform, ServerView toMove) {
        super(inform);
        this.toMove = toMove;
    }

}
