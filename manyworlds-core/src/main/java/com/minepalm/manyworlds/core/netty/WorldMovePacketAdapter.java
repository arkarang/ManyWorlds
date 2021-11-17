package com.minepalm.manyworlds.core.netty;

import com.minepalm.hellobungee.api.HelloAdapter;
import com.minepalm.hellobungee.netty.ByteBufUtils;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import io.netty.buffer.ByteBuf;

public class WorldMovePacketAdapter implements HelloAdapter<WorldMovePacket> {

    @Override
    public String getIdentifier() {
        return WorldMovePacket.class.getSimpleName();
    }

    @Override
    public void encode(ByteBuf buf, WorldMovePacket packet) {
        BasicPacket.write(buf, packet.getWorldInform());
        ByteBufUtils.writeString(buf, packet.getToMove().getServerName());
    }

    @Override
    public WorldMovePacket decode(ByteBuf buf) {
        WorldInform inform = BasicPacket.read(buf);
        String toMove = ByteBufUtils.readString(buf);
        return new WorldMovePacket(inform, new ServerView(toMove));
    }
}
