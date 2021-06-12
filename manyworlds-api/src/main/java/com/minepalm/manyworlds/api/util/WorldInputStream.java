package com.minepalm.manyworlds.api.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class WorldInputStream extends WorldStream{
    public WorldInputStream(ByteBuf buffer) {
        super(buffer);
    }

    public WorldInputStream(byte[] bytes){
        super(Unpooled.directBuffer().readBytes(bytes));
    }

    public byte[] read(byte[] toWrite){
        buffer.readBytes(toWrite);
        return toWrite;
    }

    public int readInt(){
        return buffer.readInt();
    }

    public byte readByte(){
        return buffer.readByte();
    }

    public short readShort(){
        return buffer.readShort();
    }

    public boolean readBoolean(){
        return buffer.readBoolean();
    }
}
