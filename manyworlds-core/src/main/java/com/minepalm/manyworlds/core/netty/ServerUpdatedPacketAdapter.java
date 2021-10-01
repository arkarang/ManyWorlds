package com.minepalm.manyworlds.core.netty;

import com.minepalm.hellobungee.api.HelloAdapter;
import com.minepalm.hellobungee.netty.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class ServerUpdatedPacketAdapter implements HelloAdapter<ServerUpdatedPacket> {
    @Override
    public String getIdentifier() {
        return ServerUpdatedPacket.class.getSimpleName();
    }

    @Override
    public void encode(ByteBuf buf, ServerUpdatedPacket packet) {
        ByteBufUtils.writeString(buf, packet.from.getServerName());
        ByteBufUtils.writeString(buf, packet.to.getServerName());
        buf.writeBoolean(packet.isOnline());
    }

    @Override
    public ServerUpdatedPacket decode(ByteBuf buf) {
        String from = ByteBufUtils.readString(buf);
        String to = ByteBufUtils.readString(buf);
        boolean online = buf.readBoolean();
        return new ServerUpdatedPacket(()->from, ()->to, online);
    }

}
