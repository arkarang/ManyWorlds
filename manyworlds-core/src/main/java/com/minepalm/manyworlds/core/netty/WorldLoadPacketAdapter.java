package com.minepalm.manyworlds.core.netty;

import com.minepalm.hellobungee.api.HelloAdapter;
import com.minepalm.hellobungee.netty.ByteBufUtils;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
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
        BasicPacket.write(buf, packet.getInform());
        buf.writeBoolean(packet.isLoad());
    }

    @Override
    public WorldLoadPacket decode(ByteBuf buf) {
        String from = ByteBufUtils.readString(buf);
        String to = ByteBufUtils.readString(buf);
        WorldInform inform = BasicPacket.read(buf);
        boolean load = buf.readBoolean();
        return new WorldLoadPacket(new ServerView(from), new ServerView(to), inform, load);
    }
}
