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
    }

    public ByteBuf write(){
        ByteBuf buf = super.write();
        buf.writeInt(sampleName.length()).writeCharSequence(sampleName, StandardCharsets.UTF_8);
        buf.writeInt(worldName.length()).writeCharSequence(worldName, StandardCharsets.UTF_8);
        return buf;
    }

    public BukkitView getTargetServer(){
        return (BukkitView) to;
    }

    public byte getPacketID() {
        return PacketTypes.WORLD_CREATE.index();
    }
}
