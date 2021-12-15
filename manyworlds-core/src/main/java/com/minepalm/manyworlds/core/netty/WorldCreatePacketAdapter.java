package com.minepalm.manyworlds.core.netty;

import com.minepalm.hellobungee.api.HelloAdapter;
import com.minepalm.manyworlds.api.entity.WorldInform;
import io.netty.buffer.ByteBuf;

public class WorldCreatePacketAdapter implements HelloAdapter<WorldCreatePacket> {

    @Override
    public String getIdentifier() {
        return WorldCreatePacket.class.getSimpleName();
    }

    @Override
    public void encode(ByteBuf buf, WorldCreatePacket packet) {
        BasicPacket.write(buf, packet.getOrigin());
        BasicPacket.write(buf, packet.getWorldInform());
    }

    @Override
    public WorldCreatePacket decode(ByteBuf buf) {
        WorldInform origin = BasicPacket.read(buf);
        WorldInform inform = BasicPacket.read(buf);
        return new WorldCreatePacket(origin, inform);
    }
}
