package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

@Getter
public class WorldLoadPacket extends BasicPacket{

    String sampleName;
    String worldName;
    boolean load;

    WorldLoadPacket(ServerView from, ServerView to, String sampleName, String worldName, boolean load) {
        super(from, to);
        this.sampleName = sampleName;
        this.worldName = worldName;
        this.load = load;
        ByteBuf buf = super.get();
        buf.writeInt(sampleName.length()).writeCharSequence(sampleName, StandardCharsets.UTF_8);
        buf.writeInt(worldName.length()).writeCharSequence(worldName, StandardCharsets.UTF_8);
        buf.writeBoolean(load);
    }

    WorldLoadPacket(ServerView from, ServerView to, WorldInfo info, boolean load) {
        this(from, to, info.getSampleName(), info.getWorldName(), load);
    }

    public byte getPacketID() {
        return PacketTypes.WORLD_LOAD_UNLOAD.index();
    }
}
