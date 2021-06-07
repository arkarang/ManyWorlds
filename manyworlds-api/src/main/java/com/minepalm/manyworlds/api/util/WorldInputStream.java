package com.minepalm.manyworlds.api.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class WorldInputStream extends WorldStream{
    public WorldInputStream(ByteBuf buffer) {
        super(buffer);
    }

    public WorldInputStream(byte[] bytes){
        super(Unpooled.buffer().readBytes(bytes));
    }
}
