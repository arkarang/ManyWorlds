package com.minepalm.manyworlds.core.netty;

import com.minepalm.hellobungee.api.HelloAdapter;
import com.minepalm.hellobungee.netty.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class WorldCreatePacketAdapter implements HelloAdapter<WorldCreatePacket> {

    @Override
    public String getIdentifier() {
        return WorldCreatePacket.class.getSimpleName();
    }

    @Override
    public void encode(ByteBuf buf, WorldCreatePacket packet) {
        ByteBufUtils.writeString(buf, packet.from.getServerName());
        ByteBufUtils.writeString(buf, packet.to.getServerName());
        ByteBufUtils.writeString(buf, packet.sampleName);
        ByteBufUtils.writeString(buf, packet.worldName);
    }

    @Override
    public WorldCreatePacket decode(ByteBuf buf) {
        String from = ByteBufUtils.readString(buf);
        String to = ByteBufUtils.readString(buf);
        String sampleName = ByteBufUtils.readString(buf);
        String worldName = ByteBufUtils.readString(buf);
        return new WorldCreatePacket(()->from, ()->to, sampleName, worldName);
    }
}
