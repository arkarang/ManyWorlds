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
        BasicPacket.write(buf, packet.getWorldInform());
        buf.writeBoolean(packet.isLoad());
    }

    @Override
    public WorldLoadPacket decode(ByteBuf buf) {
        WorldInform inform = BasicPacket.read(buf);
        boolean load = buf.readBoolean();
        return new WorldLoadPacket(inform, load);
    }
}
