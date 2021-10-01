package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import lombok.Getter;

@Getter
public class WorldLoadPacket extends BasicPacket{

    private final String sampleName;
    private final String worldName;
    private final boolean load;

    public WorldLoadPacket(ServerView from, ServerView to, String sampleName, String worldName, boolean load) {
        super(from, to);
        this.sampleName = sampleName;
        this.worldName = worldName;
        this.load = load;
    }

    public WorldLoadPacket(ServerView from, ServerView to, WorldInfo info, boolean load) {
        this(from, to, info.getSampleName(), info.getWorldName(), load);
    }

}
