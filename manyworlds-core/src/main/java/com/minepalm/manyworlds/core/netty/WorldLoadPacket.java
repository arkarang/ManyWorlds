package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.ServerView;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

@Getter
public class WorldLoadPacket extends BasicPacket{

    String worldName;
    boolean load;

    WorldLoadPacket(ServerView from, ServerView to, String worldName, boolean load) {
        super(from, to);
        this.worldName = worldName;
        this.load = load;
    }

    @Override
    public ByteBuf write() {
        ByteBuf buf = super.write();
        buf.writeInt(worldName.length()).writeCharSequence(worldName, StandardCharsets.UTF_8);
        buf.writeBoolean(load);
        return buf;
    }

    public byte getPacketID() {
        return PacketTypes.WORLD_LOAD_UNLOAD.index();
    }
}
