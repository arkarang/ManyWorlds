package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.ServerView;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

@Getter
public class WorldCreatePacket extends BasicPacket {

    String sampleName;
    String worldName;

    WorldCreatePacket(ServerView from, BukkitView to, String sampleName, String worldName) {
        super(from, to);
        this.sampleName = sampleName;
        this.worldName = worldName;
        ByteBuf buf = super.get();
        buf.writeInt(sampleName.length()).writeCharSequence(sampleName, StandardCharsets.UTF_8);
        buf.writeInt(worldName.length()).writeCharSequence(worldName, StandardCharsets.UTF_8);
    }

    public BukkitView getTargetServer(){
        return (BukkitView) to;
    }

    public byte getPacketID() {
        return PacketTypes.WORLD_CREATE.index();
    }
}
