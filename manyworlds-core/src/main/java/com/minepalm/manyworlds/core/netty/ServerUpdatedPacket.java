package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.ServerView;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class ServerUpdatedPacket extends BasicPacket{

    final boolean online;

    ServerUpdatedPacket(ServerView from, ServerView to, boolean online) {
        super(from, to);
        this.online = online;
    }

    @Override
    public ByteBuf write() {
        return super.write().writeBoolean(online);
    }

    public byte getPacketID() {
        return PacketTypes.SERVER_STATUS.index();
    }
}
