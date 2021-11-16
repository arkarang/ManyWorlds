package com.minepalm.manyworlds.core.netty;

import com.minepalm.hellobungee.api.HelloAdapter;
import com.minepalm.hellobungee.netty.ByteBufUtils;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
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
        BasicPacket.write(buf, packet.getInform());
    }

    @Override
    public WorldCreatePacket decode(ByteBuf buf) {
        String from = ByteBufUtils.readString(buf);
        String to = ByteBufUtils.readString(buf);
        WorldInform inform = BasicPacket.read(buf);
        return new WorldCreatePacket(new ServerView(from), new ServerView(to), inform);
    }
}
