package com.minepalm.manyworlds.api.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class WorldOutputStream extends WorldStream{
    public WorldOutputStream(ByteBuf buffer) {
        super(buffer);
    }

    public WorldOutputStream(byte[] bytes){
        super(Unpooled.buffer().readBytes(bytes));
    }
}
