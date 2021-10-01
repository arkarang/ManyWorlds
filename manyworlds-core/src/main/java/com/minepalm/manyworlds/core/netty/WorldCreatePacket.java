package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.ServerView;
import lombok.Getter;

@Getter
public class WorldCreatePacket extends BasicPacket {

    String sampleName;
    String worldName;

    public WorldCreatePacket(ServerView from, ServerView to, String sampleName, String worldName) {
        super(from, to);
        this.sampleName = sampleName;
        this.worldName = worldName;
    }

    public BukkitView getTargetServer(){
        return (BukkitView) to;
    }

}
