package com.minepalm.manyworlds.api.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class WorldOutputStream extends WorldStream{
    public WorldOutputStream(ByteBuf buffer) {
        super(buffer);
    }

    public WorldOutputStream(byte[] bytes){
        super(Unpooled.directBuffer().readBytes(bytes));
    }

    public void writeBytes(byte[] bytes){
        buffer.writeBytes(bytes);
    }

    public void writeInt(int i){
        buffer.writeInt(i);
    }

    public void writeBoolean(boolean b){
        buffer.writeBoolean(b);
    }

    public void writeByte(byte b){
        buffer.writeByte(b);
    }

    public void writeShort(int s){
        buffer.writeShort(s);
    }
}
