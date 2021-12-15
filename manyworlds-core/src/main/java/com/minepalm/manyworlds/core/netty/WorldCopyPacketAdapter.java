package com.minepalm.manyworlds.core.netty;

import com.minepalm.hellobungee.api.HelloAdapter;
import com.minepalm.manyworlds.api.entity.WorldInform;
import io.netty.buffer.ByteBuf;

public class WorldCopyPacketAdapter implements HelloAdapter<WorldCopyPacket> {

    @Override
    public String getIdentifier() {
        return WorldCopyPacket.class.getSimpleName();
    }


    @Override
    public void encode(ByteBuf buf, WorldCopyPacket packet) {
        BasicPacket.write(buf, packet.getWorldInform());
        BasicPacket.write(buf, packet.getToCopy());
    }

    @Override
    public WorldCopyPacket decode(ByteBuf buf) {
        WorldInform inform = BasicPacket.read(buf);
        WorldInform toCopy = BasicPacket.read(buf);
        return new WorldCopyPacket( inform, toCopy);
    }
}
