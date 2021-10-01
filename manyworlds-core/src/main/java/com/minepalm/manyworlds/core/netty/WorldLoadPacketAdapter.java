package com.minepalm.manyworlds.core.netty;

import com.minepalm.hellobungee.api.HelloAdapter;
import com.minepalm.hellobungee.netty.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class WorldLoadPacketAdapter implements HelloAdapter<WorldLoadPacket> {

    @Override
    public String getIdentifier() {
        return WorldLoadPacket.class.getSimpleName();
    }


    @Override
    public void encode(ByteBuf buf, WorldLoadPacket packet) {
        ByteBufUtils.writeString(buf, packet.getFrom().getServerName());
        ByteBufUtils.writeString(buf, packet.getTo().getServerName());
        ByteBufUtils.writeString(buf, packet.getSampleName());
        ByteBufUtils.writeString(buf, packet.getWorldName());
        buf.writeBoolean(packet.isLoad());
    }

    @Override
    public WorldLoadPacket decode(ByteBuf buf) {
        String from = ByteBufUtils.readString(buf);
        String to = ByteBufUtils.readString(buf);
        String sampleName = ByteBufUtils.readString(buf);
        String worldName = ByteBufUtils.readString(buf);
        boolean load = buf.readBoolean();
        return new WorldLoadPacket(()->from, ()->to, sampleName, worldName, load);
    }
}
