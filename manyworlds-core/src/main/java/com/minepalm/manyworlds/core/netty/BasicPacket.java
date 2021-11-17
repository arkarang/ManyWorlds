package com.minepalm.manyworlds.core.netty;

import com.minepalm.hellobungee.netty.ByteBufUtils;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import com.minepalm.manyworlds.core.WorldToken;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public abstract class BasicPacket implements WorldPacket{

    final WorldInform worldInform;

    BasicPacket(WorldInform worldInform){
        this.worldInform = worldInform;
    }

    protected static void write(ByteBuf buf, WorldInform inform){
        ByteBufUtils.writeString(buf, inform.getWorldCategory().getName());
        ByteBufUtils.writeString(buf, inform.getName());
    }

    protected static WorldInform read(ByteBuf buf){
        String category = ByteBufUtils.readString(buf);
        String name = ByteBufUtils.readString(buf);
        return new WorldInform(WorldToken.get(category), name);
    }
    
}
